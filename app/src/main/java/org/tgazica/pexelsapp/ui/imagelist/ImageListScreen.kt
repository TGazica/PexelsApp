package org.tgazica.pexelsapp.ui.imagelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import org.tgazica.pexelsapp.R
import org.tgazica.pexelsapp.ui.imagelist.model.ImageListAction
import org.tgazica.pexelsapp.ui.imagelist.model.ImageListUiState

@Composable
fun ImageListScreen(
    viewModel: ImageListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    ImageListScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListScreen(
    uiState: ImageListUiState,
    onAction: (ImageListAction) -> Unit
) {
    val listState = rememberLazyStaggeredGridState()
    val refreshState = rememberPullToRefreshState()
    val lastScrollOffset = remember { mutableFloatStateOf(0f) }
    val isFabVisible by remember {
        derivedStateOf {
            lastScrollOffset.floatValue > 0f && listState.canScrollBackward
        }
    }
    val nestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                lastScrollOffset.floatValue = available.y
                return super.onPreScroll(available, source)
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScroll),
        floatingActionButton = {
            AnimatedVisibility(
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
                visible = isFabVisible
            ) {
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0, 0)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_scroll),
                        contentDescription = ""
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .nestedScroll(refreshState.nestedScrollConnection)
        ) {
            ImageList(
                uiState = uiState,
                scrollState = listState,
                onAction = onAction
            )

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = refreshState
            )
        }

        LaunchedEffect(key1 = uiState) {
            if (uiState.isLoading && !refreshState.isRefreshing) {
                refreshState.startRefresh()
            } else if (!uiState.isLoading && refreshState.isRefreshing) {
                refreshState.endRefresh()
            }
        }

        LaunchedEffect(key1 = refreshState.isRefreshing) {
            if (!uiState.isLoading && refreshState.isRefreshing) {
                onAction(ImageListAction.RefreshImages)
            }
        }
    }
}

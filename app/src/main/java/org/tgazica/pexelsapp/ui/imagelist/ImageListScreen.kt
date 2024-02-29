package org.tgazica.pexelsapp.ui.imagelist

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
    val refreshState = rememberPullToRefreshState()
    Box(
        modifier = Modifier.nestedScroll(refreshState.nestedScrollConnection)
    ) {
        ImageList(
            uiState = uiState,
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

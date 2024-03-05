package org.tgazica.pexelsapp.ui.imagedetails

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.tgazica.pexelsapp.R
import org.tgazica.pexelsapp.ui.shared.model.ImageUiState
import org.tgazica.pexelsapp.ui.shared.PexelsImage
import org.tgazica.pexelsapp.ui.shared.PexelsTopBar
import org.tgazica.pexelsapp.ui.util.openLink
import org.tgazica.pexelsapp.ui.util.zoomable

private const val MAX_ZOOM = 10f
private const val MIN_ZOOM = 1f

/**
 * Stateful implementation for the image details screen.
 *
 * @param viewModel The [ImageDetailsViewModel] containing presentation logic for the image details.
 */
@Composable
fun ImageDetailsScreen(
    viewModel: ImageDetailsViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    ImageDetailsScreen(
        uiState = uiState,
    )
}

/**
 * Stateless implementation of the image details screen. Is driven by external [ImageUiState].
 *
 * @param uiState The image for which we wish to display the details for.
 */
@Composable
fun ImageDetailsScreen(
    uiState: ImageUiState,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isFullscreen by remember { mutableStateOf(false) }
    var shouldShowOverlay by remember { mutableStateOf(false) }

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFullscreen) isFullscreen = false
            }
        }
    }

    LaunchedEffect(key1 = isFullscreen) {
        callback.isEnabled = isFullscreen
        if (isFullscreen) {
            backPressedDispatcher?.addCallback(lifecycleOwner, callback)
        } else {
            shouldShowOverlay = false
            callback.remove()
        }
    }

    Scaffold(
        topBar = {
            if (!isFullscreen) {
                PexelsTopBar(
                    title = uiState.author,
                    iconRes = R.drawable.ic_back,
                    onTitleClicked = { context.openLink(uiState.authorUrl) },
                )
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
        ) {
            val scrollState = rememberScrollState()
            val listModifier = remember(isFullscreen) {
                val modifier = Modifier.fillMaxSize()
                if (isFullscreen) {
                    modifier
                } else {
                    modifier.verticalScroll(scrollState)
                }
            }

            Column(
                modifier = listModifier,
                verticalArrangement = if (isFullscreen) Arrangement.SpaceAround else Arrangement.Top,
            ) {
                ImageDetailsImage(
                    uiState = uiState,
                    isFullscreen = isFullscreen,
                    onImageClicked = {
                        if (!isFullscreen) {
                            isFullscreen = true
                        } else {
                            shouldShowOverlay = !shouldShowOverlay
                        }
                    },
                )

                if (!isFullscreen) {
                    ImageDetails(uiState = uiState)
                }
            }

            if (isFullscreen && shouldShowOverlay) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                        .background(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.4f),
                        )
                        .clip(shape = CircleShape)
                        .clickable { isFullscreen = false }
                        .padding(12.dp),
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "",
                )
            }
        }
    }
}

@Composable
private fun ImageDetailsImage(
    uiState: ImageUiState,
    isFullscreen: Boolean,
    onImageClicked: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(uiState.aspectRatio),
    ) {
        val density = LocalDensity.current
        val parentSize =
            Size(density.run { maxWidth.toPx() }, density.run { maxHeight.toPx() })

        val imageModifier = remember(Pair(isFullscreen, parentSize)) {
            if (isFullscreen) {
                Modifier
                    .zoomable(
                        minZoom = MIN_ZOOM,
                        maxZoom = MAX_ZOOM,
                        parentSize = parentSize,
                        onClick = onImageClicked,
                    )
            } else {
                Modifier.clickable(onClick = onImageClicked)
            }
        }

        PexelsImage(
            modifier = imageModifier,
            imageUrl = uiState.imageUrl,
            scale = if (!isFullscreen) ContentScale.FillBounds else ContentScale.Fit,
        )
    }
}

@Composable
private fun ImageDetails(
    uiState: ImageUiState,
) {
    val context = LocalContext.current

    Column {
        ImageDetailsText(
            text = "See more from ${uiState.author}",
            onClick = { context.openLink(uiState.authorUrl) },
        )

        ImageDetailsText(
            text = "See full image details",
            onClick = { context.openLink(uiState.url) },
        )

        ImageDetailsText(
            text = "Dimension: ${uiState.imageDimensions}",
        )

        if (uiState.imageDescription.isNotBlank()) {
            ImageDetailsText(text = "Description: ${uiState.imageDescription}")
        }
    }
}

@Composable
private fun ImageDetailsText(
    text: String,
    onClick: (() -> Unit)? = null,
) {
    Text(
        modifier = Modifier
            .clickable(enabled = onClick != null, onClick = onClick ?: {})
            .padding(8.dp),
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

package org.tgazica.pexelsapp.ui.imagedetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.tgazica.pexelsapp.R
import org.tgazica.pexelsapp.ui.model.ImageUiState
import org.tgazica.pexelsapp.ui.shared.image.PexelsImage
import org.tgazica.pexelsapp.ui.shared.topbar.PexelsTopBar
import org.tgazica.pexelsapp.ui.util.openLink

private const val MAX_ZOOM = 10f
private const val MIN_ZOOM = 1f

/**
 * Stateful implementation for the image details screen.
 *
 * @param viewModel The [ImageDetailsViewModel] containing presentation logic for the image details.
 */
@Composable
fun ImageDetailsScreen(
    viewModel: ImageDetailsViewModel
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

    Scaffold(
        topBar = {
            PexelsTopBar(
                title = uiState.author,
                iconRes = R.drawable.ic_back,
                onTitleClicked = { context.openLink(uiState.authorUrl) }
            )
        }
    ) {
        // Used to hide/show the details when the image is zoomed in or not
        val scale = remember { mutableFloatStateOf(MIN_ZOOM) }
        val areDetailsVisible by remember { derivedStateOf { scale.floatValue == MIN_ZOOM } }

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(uiState.aspectRatio)
            ) {
                val density = LocalDensity.current
                val parentSize =
                    Size(density.run { maxWidth.toPx() }, density.run { maxHeight.toPx() })

                PexelsImage(
                    modifier = Modifier.zoomable(
                        parentSize = parentSize,
                        onScaleChanged = { scale.floatValue = it }
                    ),
                    imageUrl = uiState.imageUrl,
                    scale = ContentScale.FillBounds
                )
            }

            AnimatedVisibility(
                visible = areDetailsVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    ImageDetailsText(
                        text = "See more from ${uiState.author}",
                        onClick = { context.openLink(uiState.authorUrl) }
                    )

                    ImageDetailsText(
                        text = "See full image details",
                        onClick = { context.openLink(uiState.url) }
                    )

                    if (uiState.imageDescription.isNotBlank()) {
                        ImageDetailsText(text = "Description: ${uiState.imageDescription}")
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageDetailsText(
    text: String,
    onClick: (() -> Unit)? = null
) {
    Text(
        modifier = Modifier
            .clickable(enabled = onClick != null, onClick = onClick ?: {})
            .padding(8.dp),
        text = text,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun Modifier.zoomable(
    parentSize: Size,
    onScaleChanged: (Float) -> Unit = {}
): Modifier {
    var imageSize by remember { mutableStateOf(Size(0f, 0f)) }

    var currentScale by remember { mutableFloatStateOf(MIN_ZOOM) }
    var translation by remember { mutableStateOf(Offset(0f, 0f)) }

    val scale by animateFloatAsState(targetValue = currentScale, label = "scale")

    LaunchedEffect(key1 = currentScale) {
        onScaleChanged(currentScale)
    }

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            translationX = translation.x
            translationY = translation.y
        }
        .onGloballyPositioned {
            imageSize = Size(it.size.width.toFloat(), it.size.height.toFloat())
        }
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                currentScale = (zoom * currentScale).coerceIn(MIN_ZOOM, MAX_ZOOM)

                val maxTranslation = calculateMaxOffset(
                    imageSize = imageSize,
                    scale = currentScale,
                    parentSize = parentSize,
                )

                val newTranslationX = translation.x + pan.x * currentScale
                val newTranslationY = translation.y + pan.y * currentScale

                translation = Offset(
                    newTranslationX.coerceIn(-maxTranslation.x, maxTranslation.x),
                    newTranslationY.coerceIn(-maxTranslation.y, maxTranslation.y),
                )
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    currentScale = if (currentScale > MIN_ZOOM) {
                        MIN_ZOOM
                    } else {
                        MAX_ZOOM / 2
                    }

                    val maxTranslation = calculateMaxOffset(
                        imageSize = imageSize,
                        scale = currentScale,
                        parentSize = parentSize,
                    )

                    val newTranslationX = translation.x + currentScale
                    val newTranslationY = translation.y + currentScale

                    translation = Offset(
                        newTranslationX.coerceIn(-maxTranslation.x, maxTranslation.x),
                        newTranslationY.coerceIn(-maxTranslation.y, maxTranslation.y),
                    )
                }
            )
        }
}

private fun calculateMaxOffset(imageSize: Size, scale: Float, parentSize: Size): Offset {
    val maxTranslationY = calculateMaxOffsetPerAxis(imageSize.height, scale, parentSize.height)
    val maxTranslationX = calculateMaxOffsetPerAxis(imageSize.width, scale, parentSize.width)
    return Offset(maxTranslationX, maxTranslationY)
}

private fun calculateMaxOffsetPerAxis(axisSize: Float, scale: Float, parentAxisSize: Float): Float {
    return (axisSize * scale - parentAxisSize).coerceAtLeast(0f) / 2
}




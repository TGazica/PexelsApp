package org.tgazica.pexelsapp.ui.imagedetails

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import org.tgazica.pexelsapp.R
import org.tgazica.pexelsapp.ui.model.ImageUiState
import org.tgazica.pexelsapp.ui.shared.image.PexelsImage
import org.tgazica.pexelsapp.ui.shared.topbar.PexelsTopBar

private const val MAX_ZOOM = 10f
private const val MIN_ZOOM = 1f

@Composable
fun ImageDetailsScreen(
    viewModel: ImageDetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    ImageDetailsScreen(
        uiState = uiState,
    )
}

@Composable
fun ImageDetailsScreen(
    uiState: ImageUiState,
) {
    Scaffold(
        topBar = {
            PexelsTopBar(
                title = uiState.author,
                iconRes = R.drawable.ic_back
            )
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current
            val parentSize = Size(density.run { maxWidth.toPx() }, density.run { maxHeight.toPx() })
            var imageSize by remember { mutableStateOf(Size(0f, 0f)) }

            var currentScale by remember { mutableStateOf(MIN_ZOOM) }
            var translation by remember { mutableStateOf(Offset(0f, 0f)) }

            val scale by animateFloatAsState(targetValue = currentScale)

            PexelsImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(uiState.aspectRatio)
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
                                if (currentScale > MIN_ZOOM) {
                                    currentScale = MIN_ZOOM
                                } else {
                                    currentScale = MAX_ZOOM / 2
                                }
                            }
                        )
                    },
                imageUrl = uiState.imageUrl,
                scale = ContentScale.FillBounds
            )
        }
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




package org.tgazica.pexelsapp.ui.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * Custom modifier for zoomable content. Due to interactions with the tap and drag gestures, click
 * listener should be implemented here for proper tap detection.
 *
 * @param minZoom Minimal amount the image can be zoomed out.
 * @param maxZoom Maximal amount the image can be zoomed in.
 * @param parentSize The size of the parent container.
 * @param onClick The click listener for zoomable composables. Reason it is here is to be able to
 * properly detect each of user taps and interactions with the screen.
 */
fun Modifier.zoomable(
    minZoom: Float = 1f,
    maxZoom: Float = Float.MAX_VALUE,
    parentSize: Size,
    onClick: () -> Unit,
) = composed {
    var imageSize by remember { mutableStateOf(Size(0f, 0f)) }

    var currentScale by remember { mutableFloatStateOf(minZoom) }
    var translation by remember { mutableStateOf(Offset(0f, 0f)) }

    val scale by animateFloatAsState(targetValue = currentScale, label = "scale")

    this
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
                currentScale = (zoom * currentScale).coerceIn(minZoom, maxZoom)

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
                onTap = {
                    onClick()
                },
                onDoubleTap = {
                    currentScale = if (currentScale > minZoom) {
                        minZoom
                    } else {
                        maxZoom / 2
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
                },
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

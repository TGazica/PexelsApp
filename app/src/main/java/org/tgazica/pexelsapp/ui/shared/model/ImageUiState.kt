package org.tgazica.pexelsapp.ui.shared.model

import org.tgazica.pexelsapp.data.remote.model.ApiImage
import kotlin.math.max

/**
 * State of a single image.
 */
data class ImageUiState(
    val id: Int = 0,
    val url: String = "",
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val author: String = "",
    val authorUrl: String = "",
    val width: Int = 1,
    val height: Int = 1,
    val imageDescription: String = "",
) {
    val aspectRatio: Float = max(0.1f, width.toFloat() / height)
    val imageDimensions: String = "${width}x$height"
}

/**
 * Mapper used to map the [ApiImage] to [ImageUiState]
 */
fun ApiImage.toImageUiState() = ImageUiState(
    id = id,
    url = url,
    imageUrl = src.original,
    thumbnailUrl = src.medium,
    author = photographer,
    authorUrl = photographerUrl,
    width = width,
    height = height,
    imageDescription = alt,
)

package org.tgazica.pexelsapp.ui.model

import org.tgazica.pexelsapp.data.remote.model.ApiImage

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
    val aspectRatio: Float = 1f,
    val imageDescription: String = "",

)

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
    aspectRatio = width.toFloat() / height,
    imageDescription = alt
)

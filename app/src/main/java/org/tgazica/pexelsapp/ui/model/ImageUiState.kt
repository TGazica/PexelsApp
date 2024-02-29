package org.tgazica.pexelsapp.ui.model

import org.tgazica.pexelsapp.data.remote.model.ApiImage

data class ImageUiState(
    val id: Int = 0,
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val author: String = "",
    val aspectRatio: Float = 1f,
    val imageDescription: String = ""
)

fun ApiImage.toImageUiState() = ImageUiState(
    id = id,
    imageUrl = src.original,
    thumbnailUrl = src.medium,
    author = photographer,
    aspectRatio = width.toFloat() / height,
    imageDescription = alt
)

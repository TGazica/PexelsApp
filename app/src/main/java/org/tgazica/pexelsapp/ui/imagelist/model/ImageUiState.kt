package org.tgazica.pexelsapp.ui.imagelist.model

import org.tgazica.pexelsapp.data.remote.model.ApiImage

data class ImageUiState(
    val imageUrl: String,
    val thumbnailUrl: String,
    val author: String,
    val aspectRatio: Float
)

fun ApiImage.toImageUiState() = ImageUiState(
    imageUrl = url,
    thumbnailUrl = src.medium,
    author = photographer,
    aspectRatio = width.toFloat() / height
)

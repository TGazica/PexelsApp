package org.tgazica.pexelsapp.ui.imagelist.model

import org.tgazica.pexelsapp.data.remote.model.ApiImage

data class ImageUiState(
    val imageUrl: String,
    val width: Int,
    val height: Int,
    val thumbnailUrl: String,
    val author: String
) {
    init {
        println("$imageUrl: $width, $height")
    }

    val aspectRatio = if (width > height) height.toFloat() / width else width.toFloat() / height

}

fun ApiImage.toImageUiState() = ImageUiState(
    imageUrl = url,
    thumbnailUrl = src.medium,
    author = photographer,
    width = width,
    height = height
)

package org.tgazica.pexelsapp.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiImage(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val src: ApiImageSrc,
    val photographer: String,
    @SerialName("avg_color")
    val avgColor: String
)

@Serializable
data class ApiImageSrc(
    val original: String,
    val large: String,
    val medium: String,
    val portrait: String,
    val landscape: String,
)
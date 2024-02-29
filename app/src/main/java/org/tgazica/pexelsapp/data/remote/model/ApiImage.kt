package org.tgazica.pexelsapp.data.remote.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ApiImage(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val src: ApiImageSrc,
    val photographer: String,
    @JsonNames("avg_color")
    val avgColor: String,
    val alt: String
)

@Serializable
data class ApiImageSrc(
    val original: String,
    val large: String,
    val medium: String,
    val portrait: String,
    val landscape: String,
)

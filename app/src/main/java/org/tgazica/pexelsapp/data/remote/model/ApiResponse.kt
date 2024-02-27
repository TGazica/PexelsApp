package org.tgazica.pexelsapp.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val page: Int,
    @SerialName("per_page")
    val perPage: Int,
    val photos: List<ApiImage>,
)
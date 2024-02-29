package org.tgazica.pexelsapp.data.remote.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ApiResponse<T>(
    @JsonNames("photos")
    val data: List<T>,
    @JsonNames("next_page")
    val nextPage: String?
)

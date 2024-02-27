package org.tgazica.pexelsapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.tgazica.pexelsapp.BuildConfig
import org.tgazica.pexelsapp.data.baseUrl
import org.tgazica.pexelsapp.data.remote.model.ApiImage
import org.tgazica.pexelsapp.data.remote.model.ApiResponse

class ImagesService(
    private val httpClient: HttpClient
) {

    suspend fun getImages(): List<ApiImage> {
        return httpClient.get("$baseUrl/curated") {
            header("Authorization", BuildConfig.pexelsApiKey)
        }.body<ApiResponse>().photos
    }

}
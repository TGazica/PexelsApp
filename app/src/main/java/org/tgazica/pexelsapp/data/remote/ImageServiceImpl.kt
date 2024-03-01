package org.tgazica.pexelsapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.tgazica.pexelsapp.data.baseUrl
import org.tgazica.pexelsapp.data.remote.model.ApiImage
import org.tgazica.pexelsapp.data.remote.model.ApiResponse

internal class ImageServiceImpl(
    private val httpClient: HttpClient
): ImageService {

    override suspend fun getImages(page: Int): ApiResponse<ApiImage> {
        return httpClient.get("$baseUrl/curated") {
            parameter(PARAMETER_PAGE, page)
            parameter(PARAMETER_PER_PAGE, PAGE_SIZE)
        }.body()
    }

    companion object {
        private const val PARAMETER_PAGE = "page"
        private const val PARAMETER_PER_PAGE = "per_page"

        private const val PAGE_SIZE = 20
    }

}

package org.tgazica.pexelsapp.data.remote

import org.tgazica.pexelsapp.data.remote.model.ApiImage
import org.tgazica.pexelsapp.data.remote.model.ApiResponse

interface ImageService {
    suspend fun getImages(page: Int): ApiResponse<ApiImage>
}

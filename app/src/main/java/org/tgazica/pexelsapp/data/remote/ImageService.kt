package org.tgazica.pexelsapp.data.remote

import org.tgazica.pexelsapp.data.remote.model.ApiImage
import org.tgazica.pexelsapp.data.remote.model.ApiResponse

/**
 * Interface used to communicate with remote Pexels api.
 */
interface ImageService {
    /**
     * @param page The page number we wish to load.
     *
     * @return [ApiResponse] populated with all of the [ApiImage]s.
     */
    suspend fun getImages(page: Int): ApiResponse<ApiImage>
}

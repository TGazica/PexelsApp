package org.tgazica.pexelsapp.data.repo

import kotlinx.coroutines.flow.Flow
import org.tgazica.pexelsapp.data.remote.model.ApiImage

/**
 * Interface between the presentation and data layers used to retrieve and update the images
 * when requested.
 */
interface ImageRepo {
    /**
     * @return Stream of [ApiImage] lists.
     */
    suspend fun observeImages(): Flow<List<ApiImage>>

    /**
     * @param imageId Id of the image we wish to query.
     *
     * @return a single image.
     */
    suspend fun getImage(imageId: Int): ApiImage

    /**
     * Requests the next page to be loaded.
     */
    suspend fun loadNextPage()

    /**
     * Requests the refresh of the images. This will also clear the cache.
     */
    suspend fun refreshImages()
}

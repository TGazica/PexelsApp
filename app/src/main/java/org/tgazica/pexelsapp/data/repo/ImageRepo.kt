package org.tgazica.pexelsapp.data.repo

import kotlinx.coroutines.flow.Flow
import org.tgazica.pexelsapp.data.remote.model.ApiImage
import org.tgazica.pexelsapp.data.model.Result

interface ImageRepo {
    suspend fun observeImages(): Flow<Result<List<ApiImage>>>
    suspend fun getImageById(imageId: Int): ApiImage
    suspend fun loadNextPage()
    suspend fun refreshImages()
}

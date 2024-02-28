package org.tgazica.pexelsapp.data.repo

import kotlinx.coroutines.flow.Flow
import org.tgazica.pexelsapp.data.remote.model.ApiImage

interface ImageRepo {
    suspend fun observeImages(): Flow<List<ApiImage>>
    suspend fun loadNextPage()
    suspend fun refreshImages()
}
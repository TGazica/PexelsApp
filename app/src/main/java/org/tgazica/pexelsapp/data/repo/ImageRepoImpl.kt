package org.tgazica.pexelsapp.data.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import org.tgazica.pexelsapp.data.remote.ImageService
import org.tgazica.pexelsapp.data.remote.model.ApiImage
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import org.tgazica.pexelsapp.data.model.Result
import org.tgazica.pexelsapp.data.model.dataOrNull
import org.tgazica.pexelsapp.data.model.isLoading
import org.tgazica.pexelsapp.data.cache.AppCacheStorage

class ImageRepoImpl(
    private val imageService: ImageService,
    private val cache: AppCacheStorage
) : ImageRepo {

    private val imagesCache: MutableStateFlow<List<ApiImage>> = MutableStateFlow(emptyList())
    private val resultImages: MutableStateFlow<Result<List<ApiImage>>> =
        MutableStateFlow(Result.Success(imagesCache.value))

    private val hasReachedEnd: AtomicBoolean = AtomicBoolean(false)
    private val currentPage: AtomicInteger = AtomicInteger(0)

    override suspend fun observeImages(): Flow<Result<List<ApiImage>>> = resultImages.onStart {
        if (resultImages.value.dataOrNull().isNullOrEmpty()) loadNextPage()
    }

    override suspend fun getImageById(imageId: Int): ApiImage {
        return imagesCache.value.first { it.id == imageId }
    }

    override suspend fun loadNextPage() {
        try {
            if (resultImages.value.isLoading() || hasReachedEnd.get()) return
            resultImages.update { Result.Loading() }

            val page = currentPage.incrementAndGet()
            val imagesResponse = imageService.getImages(page)

            hasReachedEnd.set(imagesResponse.nextPage == null)

            val apiImages = imagesResponse.data

            val images = imagesCache.updateAndGet { (it + apiImages).distinctBy { it.id } }
            resultImages.update { Result.Success(images) }
        } catch (e: Exception) {
            resultImages.update { Result.Error(e) }
        }
    }

    override suspend fun refreshImages() {
        try {
            if (resultImages.value.isLoading()) return
            resultImages.update { Result.Loading() }

            cache.clearCache()

            currentPage.set(1)

            val apiImages = imageService.getImages(1)
            val images = imagesCache.updateAndGet { apiImages.data }
            resultImages.update { Result.Success(images) }
        } catch (e: Exception) {
            resultImages.update { Result.Error(e) }
        }
    }
}

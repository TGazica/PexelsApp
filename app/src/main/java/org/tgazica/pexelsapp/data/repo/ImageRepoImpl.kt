package org.tgazica.pexelsapp.data.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.tgazica.pexelsapp.data.remote.ImageService
import org.tgazica.pexelsapp.data.remote.model.ApiImage
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class ImageRepoImpl(
    private val imageService: ImageService
): ImageRepo {

    private val images: MutableStateFlow<List<ApiImage>> = MutableStateFlow(emptyList())

    private val hasReachedEnd: AtomicBoolean = AtomicBoolean(false)
    private val currentPage: AtomicInteger = AtomicInteger(0)
    private val isLoading: AtomicBoolean = AtomicBoolean(false)

    override suspend fun observeImages(): Flow<List<ApiImage>> = images.onStart {
        if (images.value.isEmpty()) loadNextPage()
    }

    override suspend fun loadNextPage() {
        if (isLoading.get() || hasReachedEnd.get()) return
        isLoading.set(true)

        val page = currentPage.incrementAndGet()
        val imagesResponse = imageService.getImages(page)

        hasReachedEnd.set(imagesResponse.nextPage == null)

        val apiImages = imagesResponse.data

        if (apiImages.isNotEmpty()) images.update { (it + apiImages).distinctBy { it.id } }

        isLoading.set(false)
    }

    override suspend fun refreshImages() {
        if (isLoading.get()) return
        isLoading.set(true)

        currentPage.set(1)
        val apiImages = imageService.getImages(1)
        images.update { apiImages.data }

        isLoading.set(false)
    }
}
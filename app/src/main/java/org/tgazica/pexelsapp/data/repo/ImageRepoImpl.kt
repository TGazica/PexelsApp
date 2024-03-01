package org.tgazica.pexelsapp.data.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.tgazica.pexelsapp.data.cache.AppCacheStorage
import org.tgazica.pexelsapp.data.remote.ImageService
import org.tgazica.pexelsapp.data.remote.model.ApiImage
import org.tgazica.pexelsapp.util.NetworkConnectionListener
import org.tgazica.pexelsapp.util.NoInternetException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

internal class ImageRepoImpl(
    private val imageService: ImageService,
    private val cache: AppCacheStorage,
    networkConnectionListener: NetworkConnectionListener,
) : ImageRepo {

    private val imagesCache: MutableStateFlow<List<ApiImage>> = MutableStateFlow(emptyList())
    private val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val hasInternet = networkConnectionListener.isNetworkAvailable

    private val hasReachedEnd: AtomicBoolean = AtomicBoolean(false)
    private val currentPage: AtomicInteger = AtomicInteger(0)
    private val hasReachedCacheEnd: AtomicBoolean = AtomicBoolean(false)

    override suspend fun observeImages(): Flow<List<ApiImage>> = hasInternet.onEach { hasInternet ->
        if (imagesCache.value.isEmpty() && hasInternet) loadNextPage()
    }.flatMapLatest { imagesCache }

    override suspend fun observeLoadingState(): Flow<Boolean> = isLoading

    override suspend fun getImageById(imageId: Int): ApiImage {
        return imagesCache.value.first { it.id == imageId }
    }

    override suspend fun loadNextPage() {
        queryData {
            val page = currentPage.incrementAndGet()
            val imagesResponse = imageService.getImages(page)
            hasReachedEnd.set(imagesResponse.nextPage == null)
            val apiImages = imagesResponse.data
            imagesCache.update { (it + apiImages).distinctBy { it.id } }
        }
    }

    override suspend fun refreshImages() {
        queryData(cancelWithoutConnection = true) {
            cache.clearCache()
            currentPage.set(1)
            val apiImages = imageService.getImages(1)
            imagesCache.update { apiImages.data }
        }
    }

    /**
     * Used to query remote data and to handle errors that may come from the remote server.
     *
     * @param cancelWithoutConnection If the request should be canceled on no network connection
     * skipping loading from cache.
     * @param block the operation we wish to execute.
     */
    private suspend fun queryData(
        cancelWithoutConnection: Boolean = false,
        block: suspend () -> Unit,
    ) {
        if (shouldThrowNoInternetException(cancelWithoutConnection)) throw NoInternetException()

        if (isLoading.value) return
        isLoading.update { true }
        try {
            block()
            hasReachedCacheEnd.set(false)
        } catch (exception: Exception) {
            throw if (!hasInternet.value) {
                hasReachedCacheEnd.set(true)
                NoInternetException()
            } else {
                exception
            }
        } finally {
            isLoading.update { false }
        }
    }

    /**
     * @param cancelWithoutConnection If the request should be canceled on no network connection
     * skipping loading from cache.
     *
     * @return whether the request should be canceled due to no network connection or not.
     */
    private fun shouldThrowNoInternetException(cancelWithoutConnection: Boolean): Boolean {
        return (cancelWithoutConnection && !hasInternet.value) ||
            (hasReachedCacheEnd.get() && !hasInternet.value)
    }
}

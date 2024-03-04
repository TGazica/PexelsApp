package org.tgazica.pexelsapp.ui.imagelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.tgazica.pexelsapp.data.repo.ImageRepo
import org.tgazica.pexelsapp.ui.imagelist.model.ImageListUiState
import org.tgazica.pexelsapp.ui.model.ImageUiState
import org.tgazica.pexelsapp.ui.model.toImageUiState

class ImageListViewModel(
    private val imageRepo: ImageRepo,
) : ViewModel() {

    private val error: MutableStateFlow<Throwable?> = MutableStateFlow(null)

    private val backgroundScope =
        viewModelScope + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            error.update { throwable }
        }

    private val images: StateFlow<List<ImageUiState>> = observeData(emptyList()) {
        imageRepo.observeImages().mapLatest { it.map { it.toImageUiState() } }
    }
    private val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState: StateFlow<ImageListUiState> = combine(
        images,
        isLoading,
        error,
    ) { images, isLoading, error ->
        ImageListUiState(
            images = images,
            isLoading = isLoading,
            error = error,
        )
    }.onStart {
        if (images.value.isEmpty()) loadNextPage()
    }.stateIn(
        scope = backgroundScope,
        started = SharingStarted.WhileSubscribed(STATE_DURATION_MILLIS),
        initialValue = ImageListUiState(),
    )

    fun loadNextPage() {
        queryData {
            imageRepo.loadNextPage()
        }
    }

    fun refreshImages() {
        queryData {
            imageRepo.refreshImages()
        }
    }

    private fun queryData(block: suspend () -> Unit) {
        backgroundScope.launch {
            if (isLoading.value) return@launch
            try {
                isLoading.update { true }
                block()
                isLoading.update { false }
            } catch (e: Exception) {
                isLoading.update { false }
                error.update { it }
                throw e
            }
        }
    }

    private fun <T> observeData(
        initialValue: T,
        source: suspend () -> Flow<T>,
    ): StateFlow<T> = flow {
        emitAll(source())
    }.stateIn(
        scope = backgroundScope,
        started = SharingStarted.WhileSubscribed(STATE_DURATION_MILLIS),
        initialValue = initialValue,
    )

    companion object {
        private const val STATE_DURATION_MILLIS: Long = 5000
    }
}

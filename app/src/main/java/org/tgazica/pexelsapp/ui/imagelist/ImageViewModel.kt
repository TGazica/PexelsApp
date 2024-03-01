package org.tgazica.pexelsapp.ui.imagelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.tgazica.pexelsapp.data.model.isLoading
import org.tgazica.pexelsapp.data.model.onError
import org.tgazica.pexelsapp.data.model.onSuccess
import org.tgazica.pexelsapp.data.repo.ImageRepo
import org.tgazica.pexelsapp.ui.imagelist.model.ImageListUiState
import org.tgazica.pexelsapp.ui.model.ImageUiState
import org.tgazica.pexelsapp.ui.model.toImageUiState

class ImageViewModel(
    private val imageRepo: ImageRepo,
) : ViewModel() {

    private val backgroundScope = viewModelScope + Dispatchers.IO

    private val images = MutableStateFlow(emptyList<ImageUiState>())
    private val isLoading = MutableStateFlow(false)

    val uiState: StateFlow<ImageListUiState> = combine(
        images,
        isLoading
    ) { images, isLoading ->
        ImageListUiState(
            images = images,
            isLoading = isLoading,
        )
    }.stateIn(
        scope = backgroundScope,
        started = SharingStarted.WhileSubscribed(STATE_DURATION_MILLIS),
        initialValue = ImageListUiState()
    )

    init {
        backgroundScope.launch {
            imageRepo.observeImages().collectLatest { result ->
                isLoading.update { result.isLoading() }
                result.onSuccess { imagesResult ->
                    images.update { imagesResult.map { it.toImageUiState() } }
                }.onError {
                    // TODO show error to the user
                }
            }
        }
    }

    fun loadNextPage() {
        backgroundScope.launch {
            imageRepo.loadNextPage()
        }
    }

    fun refreshImages() {
        backgroundScope.launch {
            imageRepo.refreshImages()
        }
    }

    companion object {
        private const val STATE_DURATION_MILLIS: Long = 5000
    }
}
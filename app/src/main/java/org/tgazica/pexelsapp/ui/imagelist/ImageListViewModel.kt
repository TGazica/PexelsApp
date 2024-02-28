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
import org.tgazica.pexelsapp.data.repo.ImageRepo
import org.tgazica.pexelsapp.ui.imagelist.model.ImageListAction
import org.tgazica.pexelsapp.ui.imagelist.model.ImageListUiState
import org.tgazica.pexelsapp.ui.imagelist.model.ImageUiState
import org.tgazica.pexelsapp.ui.imagelist.model.toImageUiState
import java.lang.Exception

class ImageListViewModel(
    private val imageRepo: ImageRepo
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
            imageRepo.observeImages().collectLatest { apiImages ->
                images.update { apiImages.map { it.toImageUiState() } }
            }
        }
    }

    fun onAction(action: ImageListAction) {
        when (action) {
            is ImageListAction.ImageClick -> {
                // open image
            }

            ImageListAction.LoadNextPage -> loadNextPage()
            ImageListAction.RefreshImages -> refreshImages()
            ImageListAction.ScrollToTop -> {
                // scroll to top
            }
        }
    }

    private fun loadNextPage() {
        backgroundScope.launch {
            try {
                isLoading.update { true }
                imageRepo.loadNextPage()
            }catch (e: Exception) {
                // TODO notify user of error
                e.printStackTrace()
            }finally {
                isLoading.update { false }
            }
        }
    }

    private fun refreshImages() {
        backgroundScope.launch {
            try {
                isLoading.update { true }
                imageRepo.refreshImages()
            }catch (e: Exception) {
                // TODO notify user of error
                e.printStackTrace()
            }finally {
                isLoading.update { false }
            }
        }
    }

    companion object {
        private const val STATE_DURATION_MILLIS: Long = 5000
    }
}
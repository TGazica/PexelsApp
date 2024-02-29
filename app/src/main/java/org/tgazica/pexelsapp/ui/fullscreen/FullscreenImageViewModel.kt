package org.tgazica.pexelsapp.ui.fullscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.tgazica.pexelsapp.data.repo.ImageRepo
import org.tgazica.pexelsapp.ui.model.ImageUiState
import org.tgazica.pexelsapp.ui.model.toImageUiState

class FullscreenImageViewModel(
    private val imageId: Int,
    private val imageRepo: ImageRepo
): ViewModel() {

    private val backgroundScope = viewModelScope + Dispatchers.IO

    private val image: MutableStateFlow<ImageUiState> = MutableStateFlow(ImageUiState(id = imageId))
    private val isOverlayVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState: StateFlow<FullscreenImageUiState> = combine(
        image,
        isOverlayVisible
    ) { image, isOverlayVisible ->
        FullscreenImageUiState(
            imageUiState = image,
            isOverlayVisible = isOverlayVisible,
        )
    }.stateIn(
        scope = backgroundScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FullscreenImageUiState()
    )

    init {
        backgroundScope.launch {
            val image = imageRepo.getImageById(imageId)
            this@FullscreenImageViewModel.image.update { image.toImageUiState() }
        }
    }

    fun onImageTap() {
        isOverlayVisible.update { !it }
    }
}

package org.tgazica.pexelsapp.ui.imagedetails

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

class ImageDetailsViewModel(
    private val imageId: Int,
    private val imageRepo: ImageRepo
): ViewModel() {

    private val backgroundScope = viewModelScope + Dispatchers.IO

    private val _uiState: MutableStateFlow<ImageUiState> = MutableStateFlow(ImageUiState(id = imageId))
    val uiState: StateFlow<ImageUiState> = _uiState

    init {
        backgroundScope.launch {
            val image = imageRepo.getImageById(imageId)
            _uiState.update { image.toImageUiState() }
        }
    }
}

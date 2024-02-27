package org.tgazica.pexelsapp.ui.imagelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.tgazica.pexelsapp.data.remote.ImagesService

class ImageListViewModel(
    val imagesService: ImagesService
) : ViewModel() {

    val backgroundScope = viewModelScope + Dispatchers.IO

    val images = MutableStateFlow(ImageListUiState(emptyList()))

    init {
        backgroundScope.launch {
            val apiImages = imagesService.getImages()
            images.update { ImageListUiState(apiImages.map { it.src.medium }) }
        }
    }
}
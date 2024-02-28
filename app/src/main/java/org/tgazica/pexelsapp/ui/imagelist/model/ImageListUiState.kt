package org.tgazica.pexelsapp.ui.imagelist.model

import androidx.compose.runtime.Stable

@Stable
data class ImageListUiState(
    val images: List<ImageUiState> = emptyList(),
    val isLoading: Boolean = false
)

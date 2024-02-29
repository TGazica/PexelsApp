package org.tgazica.pexelsapp.ui.imagelist.model

import androidx.compose.runtime.Stable
import org.tgazica.pexelsapp.ui.model.ImageUiState

@Stable
data class ImageListUiState(
    val images: List<ImageUiState> = emptyList(),
    val isLoading: Boolean = false,
)

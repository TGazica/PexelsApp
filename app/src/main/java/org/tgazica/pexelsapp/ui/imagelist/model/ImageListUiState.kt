package org.tgazica.pexelsapp.ui.imagelist.model

import androidx.compose.runtime.Stable
import org.tgazica.pexelsapp.ui.model.ImageUiState

/**
 * Ui state for the image list.
 */
@Stable
data class ImageListUiState(
    val images: List<ImageUiState> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)

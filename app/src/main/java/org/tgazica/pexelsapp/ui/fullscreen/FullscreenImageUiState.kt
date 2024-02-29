package org.tgazica.pexelsapp.ui.fullscreen

import org.tgazica.pexelsapp.ui.model.ImageUiState

data class FullscreenImageUiState(
    val imageUiState: ImageUiState = ImageUiState(),
    val isOverlayVisible: Boolean = false,
)

package org.tgazica.pexelsapp.ui.imagelist.model

sealed interface ImageListAction {
    data class ImageClick(val imageUiState: ImageUiState) : ImageListAction
    data object LoadNextPage : ImageListAction
    data object RefreshImages : ImageListAction
    data object ScrollToTop : ImageListAction
}

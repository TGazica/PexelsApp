package org.tgazica.pexelsapp.ui.imagelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.tgazica.pexelsapp.ui.imagelist.model.ImageListUiState
import org.tgazica.pexelsapp.ui.model.ImageUiState

/**
 * Used to show the image list.
 *
 * @param uiState The current state of the image list.
 * @param scrollState The current scroll state of the image list.
 * @param onImageClicked Listener to notify when the user clicks on an image.
 * @param loadNextPage Notifies when the list has reached the end and needs a new page.
 */
@Composable
fun ImageList(
    uiState: ImageListUiState,
    scrollState: LazyStaggeredGridState,
    onImageClicked: (ImageUiState) -> Unit,
    loadNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(150.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        state = scrollState,
    ) {
        itemsIndexed(
            items = uiState.images,
            key = { _, image -> image.imageUrl },
        ) { index, image ->
            ImageItem(
                uiState = image,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(image.aspectRatio)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clip(shape = RoundedCornerShape(8.dp))
                    .clickable { onImageClicked(image) },
            )

            if (!uiState.isLoading && index >= uiState.images.size - 5) {
                loadNextPage()
            }
        }
    }
}

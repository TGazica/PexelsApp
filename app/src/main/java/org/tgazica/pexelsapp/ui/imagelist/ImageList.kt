package org.tgazica.pexelsapp.ui.imagelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.tgazica.pexelsapp.ui.imagelist.model.ImageListAction
import org.tgazica.pexelsapp.ui.imagelist.model.ImageListUiState

@Composable
fun ImageList(
    uiState: ImageListUiState,
    onAction: (ImageListAction) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(
            items = uiState.images,
            key = { it.imageUrl },
        ) { image ->
            ImageItem(
                uiState = image,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(image.aspectRatio)
                    .shadow(elevation = 8.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .clickable {
                        onAction(ImageListAction.ImageClick(image))
                    }
            )
        }
    }
}

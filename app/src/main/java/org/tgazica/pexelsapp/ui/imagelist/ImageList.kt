package org.tgazica.pexelsapp.ui.imagelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImageList(
    uiState: ImageListUiState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = uiState.images,
            key = { it },
        ) { imageUrl ->
            val painter = rememberAsyncImagePainter(model = imageUrl)

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                painter = painter,
                contentDescription = ""
            )
        }
    }
}

@Stable
data class ImageListUiState(
    val images: List<String>
)
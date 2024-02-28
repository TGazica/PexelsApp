package org.tgazica.pexelsapp.ui.imagelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import org.tgazica.pexelsapp.ui.imagelist.model.ImageUiState
import org.tgazica.pexelsapp.ui.theme.PexelsAppTheme

@Composable
fun ImageItem(
    uiState: ImageUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
    ) {
        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(uiState.thumbnailUrl)
            .fetcherDispatcher(Dispatchers.IO)
            .decoderDispatcher(Dispatchers.IO)
            .memoryCacheKey(uiState.thumbnailUrl)
            .diskCacheKey(uiState.thumbnailUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()

        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = imageRequest,
            contentDescription = "",
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp),
            text = uiState.author,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview
@Composable
private fun ImageItemPreview(
    @PreviewParameter(ImageItemPreviewProvider::class)
    uiState: ImageUiState
) {
    PexelsAppTheme {
        ImageItem(uiState = uiState)
    }
}

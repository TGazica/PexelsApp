package org.tgazica.pexelsapp.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import org.tgazica.pexelsapp.R
import org.tgazica.pexelsapp.ui.util.createPlaceholder

@Composable
fun PexelsImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        val placeholder = createPlaceholder(iconRes = R.drawable.ic_placeholder)
        val missingPlaceholder = createPlaceholder(iconRes = R.drawable.ic_placeholder_no_image)

        var isLoading by remember { mutableStateOf(false) }

        val context = LocalContext.current

        val imageRequest = remember(imageUrl) {
            ImageRequest.Builder(context)
                .data(imageUrl)
                .listener(
                    onStart = {
                        isLoading = true
                    },
                    onSuccess = { _, _ ->
                        isLoading = false
                    },
                    onError = { _, _ ->
                        isLoading = false
                    },
                    onCancel = {
                        isLoading = false
                    }
                )
                .fetcherDispatcher(Dispatchers.IO)
                .decoderDispatcher(Dispatchers.IO)
                .memoryCacheKey(imageUrl)
                .diskCacheKey(imageUrl)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .placeholder(placeholder)
                .error(missingPlaceholder)
                .fallback(missingPlaceholder)
                .build()
        }

        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = imageRequest,
            contentDescription = "",
            contentScale = ContentScale.Crop
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

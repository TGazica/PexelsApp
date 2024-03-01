package org.tgazica.pexelsapp.ui.shared.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import org.tgazica.pexelsapp.R
import org.tgazica.pexelsapp.ui.util.createPlaceholder

/**
 * Shared composable used to show an image loaded from the Pexels api.
 * Will show the loading spinner while the image is being loaded and will show different
 * placeholders depending on the image loading state.
 *
 * @param imageUrl The image we wish to load.
 * @param scale [ContentScale] to provide how we wish for the image to be cropped/fitted inside the
 * image container.
 * @param modifier [Modifier] used to pass custom ui modifications to the composable.
 */
@Composable
fun PexelsImage(
    imageUrl: String,
    scale: ContentScale,
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
                .crossfade(true)
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
            contentScale = scale
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

package org.tgazica.pexelsapp.ui.imagelist

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import org.tgazica.pexelsapp.R
import org.tgazica.pexelsapp.ui.imagelist.model.ImageUiState
import org.tgazica.pexelsapp.ui.theme.PexelsAppTheme


@Composable
fun ImageItem(
    uiState: ImageUiState,
    modifier: Modifier = Modifier
) {
    val placeholder = createPlaceholder()

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
            .placeholder(placeholder)
            .error(placeholder)
            .fallback(placeholder)
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
                .padding(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 4.dp),
            text = uiState.author,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun createPlaceholder(): Drawable {
    val shapeRectangle = ContextCompat.getDrawable(LocalContext.current, R.drawable.ic_placeholder) as LayerDrawable?
    val icon = shapeRectangle!!.findDrawableByLayerId(R.id.placeholderIcon) as VectorDrawable
    val background = shapeRectangle!!.findDrawableByLayerId(R.id.placeholderBackground) as GradientDrawable
    icon.setTint(MaterialTheme.colorScheme.onPrimaryContainer.toArgb())
    background.setColor(MaterialTheme.colorScheme.primaryContainer.toArgb())
    return shapeRectangle
}

@Preview(
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun ImageItemPreview(
    @PreviewParameter(ImageItemPreviewProvider::class)
    uiState: ImageUiState
) {
    PexelsAppTheme {
        ImageItem(uiState = uiState)
    }
}

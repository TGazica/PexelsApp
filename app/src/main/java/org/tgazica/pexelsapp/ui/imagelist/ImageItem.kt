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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tgazica.pexelsapp.ui.shared.model.ImageUiState
import org.tgazica.pexelsapp.ui.shared.PexelsImage

/**
 * Ui for a single image in the image list.
 *
 * @param uiState The state of the image we wish to show.
 * @param modifier Used to apply custom ui modifications on the image item.
 */
@Composable
fun ImageItem(
    uiState: ImageUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        PexelsImage(
            modifier = Modifier.fillMaxSize(),
            imageUrl = uiState.thumbnailUrl,
            scale = ContentScale.Crop,
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 4.dp),
            text = uiState.author,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

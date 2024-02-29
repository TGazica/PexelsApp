package org.tgazica.pexelsapp.ui.imagelist.model

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.tgazica.pexelsapp.ui.model.ImageUiState

internal class ImageItemPreviewProvider: PreviewParameterProvider<ImageUiState> {
    override val values: Sequence<ImageUiState> = ImageItemMockData.data
}

internal object ImageItemMockData {

    private val imageItem = ImageUiState(
        id = 0,
        imageUrl = "https://www.pexels.com/photo/woman-in-white-long-sleeved-top-and-skirt-standing-on-field-2880507/",
        aspectRatio = 1.5f,
        thumbnailUrl = "https://images.pexels.com/photos/2880507/pexels-photo-2880507.jpeg?auto=compress&cs=tinysrgb&h=350",
        author = "Some Author"
    )

    val data = sequenceOf(imageItem)
}

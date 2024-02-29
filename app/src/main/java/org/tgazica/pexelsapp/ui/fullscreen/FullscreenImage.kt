package org.tgazica.pexelsapp.ui.fullscreen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.tgazica.pexelsapp.R
import org.tgazica.pexelsapp.ui.image.PexelsImage

@Composable
fun FullscreenImage(
    viewModel: FullscreenImageViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    FullscreenImage(
        uiState = uiState,
        onImageTap = viewModel::onImageTap
    )
}

@Composable
fun FullscreenImage(
    uiState: FullscreenImageUiState,
    onImageTap: () -> Unit
) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val indicationSource = remember { MutableInteractionSource() }

    Box {
        PexelsImage(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = onImageTap,
                    indication = null,
                    interactionSource = indicationSource
                ),
            imageUrl = uiState.imageUiState.imageUrl
        )

        AnimatedVisibility(
            visible = uiState.isOverlayVisible,
            enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable { backPressedDispatcher?.onBackPressed() }
                    .padding(8.dp),
                painter = painterResource(id = R.drawable.ic_close),
                tint = Color.White,
                contentDescription = ""
            )
        }
    }
}




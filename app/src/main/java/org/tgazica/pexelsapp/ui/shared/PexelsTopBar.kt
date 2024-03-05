package org.tgazica.pexelsapp.ui.shared

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Custom top bar for the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PexelsTopBar(
    title: String,
    @DrawableRes iconRes: Int,
    onTitleClicked: (() -> Unit)? = null,
) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.clickable(
                    enabled = onTitleClicked != null,
                    onClick = onTitleClicked ?: {},
                ),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(shape = CircleShape)
                    .clickable { backPressedDispatcher?.onBackPressed() }
                    .padding(12.dp),
                painter = painterResource(id = iconRes),
                contentDescription = "",
            )
        },
    )
}

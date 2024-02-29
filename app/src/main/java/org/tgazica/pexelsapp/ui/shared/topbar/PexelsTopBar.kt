package org.tgazica.pexelsapp.ui.shared.topbar

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PexelsTopBar(
    title: String,
    @DrawableRes iconRes: Int
) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            Icon(
                modifier = Modifier.clickable { backPressedDispatcher?.onBackPressed() },
                painter = painterResource(id = iconRes),
                contentDescription = ""
            )
        }
    )
}
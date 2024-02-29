package org.tgazica.pexelsapp.ui.util

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import org.tgazica.pexelsapp.R

@Composable
fun createPlaceholder(@DrawableRes iconRes: Int): Drawable {
    val shapeRectangle = ContextCompat.getDrawable(LocalContext.current, iconRes) as LayerDrawable?
    val icon = shapeRectangle!!.findDrawableByLayerId(R.id.placeholderIcon) as VectorDrawable
    val background = shapeRectangle!!.findDrawableByLayerId(R.id.placeholderBackground) as GradientDrawable
    icon.setTint(MaterialTheme.colorScheme.onPrimaryContainer.toArgb())
    background.setColor(MaterialTheme.colorScheme.primaryContainer.toArgb())
    return shapeRectangle
}

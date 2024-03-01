package org.tgazica.pexelsapp.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Ease of use function to open a link.
 */
fun Context.openLink(link: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setData(Uri.parse(link))
    startActivity(intent)
}

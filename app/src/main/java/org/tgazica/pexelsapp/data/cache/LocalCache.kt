package org.tgazica.pexelsapp.data.cache

import android.content.Context
import java.io.File

class LocalCache(
    context: Context
): File(context.filesDir, CACHE_DIR) {

    companion object {
        private const val CACHE_DIR = "pexelsCache"
    }
}

package org.tgazica.pexelsapp.data.cache

import android.content.Context
import java.io.File

/**
 * Used to create a local cache directory for http requests.
 */
class LocalCache(
    context: Context,
) : File(context.filesDir, CACHE_DIR) {
    companion object {
        private const val CACHE_DIR = "pexelsCache"
    }
}

package org.tgazica.pexelsapp.data.remote

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.tgazica.pexelsapp.BuildConfig

fun createHttpClient(context: Context) = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }

    install(Logging) {
        logger = Logger.ANDROID
        level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
    }

    install(HttpCache) {
        publicStorage(FileStorage(context.filesDir))
    }

    defaultRequest {
        header("Authorization", BuildConfig.pexelsApiKey)
    }
}
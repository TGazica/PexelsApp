package org.tgazica.pexelsapp.data.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.tgazica.pexelsapp.BuildConfig
import org.tgazica.pexelsapp.data.remote.ImagesService

val dataModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(Logging) {
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
            }
        }
    }

    singleOf(::ImagesService)
}
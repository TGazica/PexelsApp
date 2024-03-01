package org.tgazica.pexelsapp.data.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.tgazica.pexelsapp.data.remote.ImageService
import org.tgazica.pexelsapp.data.remote.ImageServiceImpl
import org.tgazica.pexelsapp.data.cache.AppCacheStorage
import org.tgazica.pexelsapp.data.cache.LocalCache
import org.tgazica.pexelsapp.data.remote.createHttpClient
import org.tgazica.pexelsapp.data.repo.ImageRepo
import org.tgazica.pexelsapp.data.repo.ImageRepoImpl
import org.tgazica.pexelsapp.util.NetworkConnectionListener

val dataModule = module {
    singleOf(::createHttpClient)

    singleOf(::ImageServiceImpl) bind ImageService::class
    singleOf(::ImageRepoImpl) bind ImageRepo::class

    singleOf(::LocalCache)
    single<AppCacheStorage> { AppCacheStorage(directory = get()) }

    singleOf(::NetworkConnectionListener)
}

package org.tgazica.pexelsapp.ui.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import org.tgazica.pexelsapp.ui.imagelist.ImageListViewModel

val uiModule = module {
    viewModelOf(::ImageListViewModel)
}
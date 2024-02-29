package org.tgazica.pexelsapp.ui.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import org.tgazica.pexelsapp.ui.fullscreen.FullscreenImageViewModel
import org.tgazica.pexelsapp.ui.imagelist.ImageViewModel

val uiModule = module {
    viewModelOf(::ImageViewModel)
    viewModel { parameters ->
        FullscreenImageViewModel(
            imageId = parameters.get(),
            imageRepo = get()
        )
    }
}

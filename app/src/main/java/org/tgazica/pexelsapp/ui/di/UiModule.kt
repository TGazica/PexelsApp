package org.tgazica.pexelsapp.ui.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import org.tgazica.pexelsapp.ui.imagedetails.ImageDetailsViewModel
import org.tgazica.pexelsapp.ui.imagelist.ImageListViewModel

/**
 * Koin module used to inject ui dependencies.
 */
val uiModule = module {
    viewModelOf(::ImageListViewModel)
    viewModel { parameters ->
        ImageDetailsViewModel(
            imageId = parameters.get(),
            imageRepo = get(),
        )
    }
}

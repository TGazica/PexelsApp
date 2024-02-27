package org.tgazica.pexelsapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.tgazica.pexelsapp.data.di.dataModule
import org.tgazica.pexelsapp.ui.di.uiModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                listOf(
                    dataModule,
                    uiModule
                )
            )
        }
    }

}
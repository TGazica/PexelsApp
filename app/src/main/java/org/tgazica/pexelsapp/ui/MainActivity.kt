package org.tgazica.pexelsapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.tgazica.pexelsapp.ui.imagelist.ImageListScreen
import org.tgazica.pexelsapp.ui.imagelist.ImageListViewModel
import org.tgazica.pexelsapp.ui.theme.PexelsAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ImageListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PexelsAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ImageListScreen(viewModel = viewModel)
                }
            }
        }
    }
}

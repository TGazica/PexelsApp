package org.tgazica.pexelsapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.tgazica.pexelsapp.ui.imagelist.ImageList
import org.tgazica.pexelsapp.ui.imagelist.ImageListViewModel
import org.tgazica.pexelsapp.ui.theme.PexelAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ImageListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PexelAppTheme {
                val uiState by viewModel.images.collectAsState()

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ImageList(uiState = uiState)
                }
            }
        }
    }
}
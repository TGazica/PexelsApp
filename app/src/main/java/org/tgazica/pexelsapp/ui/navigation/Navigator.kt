package org.tgazica.pexelsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.tgazica.pexelsapp.ui.fullscreen.FullscreenImage
import org.tgazica.pexelsapp.ui.fullscreen.FullscreenImageViewModel
import org.tgazica.pexelsapp.ui.imagelist.ImageListScreen

@Composable
fun Navigator() {
    val controller = rememberNavController()

    NavHost(navController = controller, startDestination = ImageList.DESTINATION) {
        composable(ImageList.DESTINATION) {
            ImageListScreen(
                onImageClicked = {
                    controller.navigate(FullscreenImage.navigateTo(it.id))
                }
            )
        }

        composable(
            route = FullscreenImage.DESTINATION,
            arguments = listOf(navArgument(FullscreenImage.IMAGE_ID) { type = NavType.IntType })
        ) {
            val imageId = it.arguments?.getInt(FullscreenImage.IMAGE_ID) ?: 0
            val viewModel: FullscreenImageViewModel = koinViewModel(
                parameters = { parametersOf(imageId) }
            )
            FullscreenImage(viewModel = viewModel)
        }
    }
}

sealed interface NavDestination {
    val destination: String
}

data class ImageList(override val destination: String = DESTINATION) : NavDestination {
    companion object {
        const val DESTINATION = "imagelist"
    }
}

data class FullscreenImage(
    val image: Int,
    override val destination: String = navigateTo(imageId = image)
) : NavDestination {
    companion object {
        const val IMAGE_ID = "imageId"
        const val DESTINATION = "fullscreen/{$IMAGE_ID}"

        fun navigateTo(imageId: Int) = "fullscreen/$imageId"
    }
}

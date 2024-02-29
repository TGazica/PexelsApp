package org.tgazica.pexelsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.tgazica.pexelsapp.ui.imagedetails.ImageDetailsScreen
import org.tgazica.pexelsapp.ui.imagedetails.ImageDetailsViewModel
import org.tgazica.pexelsapp.ui.imagelist.ImageListScreen

@Composable
fun Navigator() {
    val controller = rememberNavController()

    NavHost(navController = controller, startDestination = ImageList.DESTINATION) {
        composable(ImageList.DESTINATION) {
            ImageListScreen(
                onImageClicked = {
                    controller.navigate(ImageDetails.navigateTo(it.id))
                }
            )
        }

        composable(
            route = ImageDetails.DESTINATION,
            arguments = listOf(navArgument(ImageDetails.IMAGE_ID) { type = NavType.IntType })
        ) {
            val imageId = it.arguments?.getInt(ImageDetails.IMAGE_ID) ?: 0
            val viewModel: ImageDetailsViewModel = koinViewModel(
                parameters = { parametersOf(imageId) }
            )
            ImageDetailsScreen(viewModel = viewModel)
        }
    }
}

sealed interface NavDestination {
    val destination: String
}

data class ImageList(override val destination: String = DESTINATION) : NavDestination {
    companion object {
        const val DESTINATION = "list"
    }
}

data class ImageDetails(
    val image: Int,
    override val destination: String = navigateTo(imageId = image)
) : NavDestination {
    companion object {
        const val IMAGE_ID = "imageId"
        const val DESTINATION = "details/{$IMAGE_ID}"

        fun navigateTo(imageId: Int) = "details/$imageId"
    }
}

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

/**
 * Entry point to the apps navigation. Keeps track of the current screen, as well as navigates
 * to the next requested screen.
 */
@Composable
fun Navigator() {
    val controller = rememberNavController()

    NavHost(navController = controller, startDestination = ImageList.DESTINATION) {
        composable(ImageList.DESTINATION) {
            ImageListScreen(
                onImageClicked = {
                    controller.navigate(ImageDetails.navigateTo(it.id))
                },
            )
        }

        composable(
            route = ImageDetails.DESTINATION,
            arguments = listOf(navArgument(ImageDetails.IMAGE_ID) { type = NavType.IntType }),
        ) {
            val imageId = it.arguments?.getInt(ImageDetails.IMAGE_ID) ?: 0
            val viewModel: ImageDetailsViewModel = koinViewModel(
                parameters = { parametersOf(imageId) },
            )
            ImageDetailsScreen(viewModel = viewModel)
        }
    }
}

/**
 * Interface for all of the destinations in the app.
 */
sealed interface NavDestination

/**
 * Class that is used to navigate to the images list screen.
 */
data object ImageList : NavDestination {
    const val DESTINATION = "list"
}

/**
 * Class that is used to navigate to the images details screen.
 */
data object ImageDetails : NavDestination {
    const val IMAGE_ID = "imageId"
    const val DESTINATION = "details/{$IMAGE_ID}"

    fun navigateTo(imageId: Int) = "details/$imageId"
}

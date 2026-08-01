package com.aura.gallery.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aura.gallery.presentation.albums.AlbumsScreen
import com.aura.gallery.presentation.components.GallerySharedViewModel
import com.aura.gallery.presentation.favorites.FavoritesScreen
import com.aura.gallery.presentation.gallery.GalleryScreen
import com.aura.gallery.presentation.player.VideoPlayerScreen
import com.aura.gallery.presentation.viewer.MediaViewerScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val sharedViewModel: GallerySharedViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Albums.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Albums.route) {
            AlbumsScreen(
                onAlbumClick = { bucketId, bucketName ->
                    navController.navigate(
                        Screen.Gallery.createRoute(bucketId, bucketName)
                    )
                }
            )
        }

        composable(
            route = Screen.Gallery.route,
            arguments = listOf(
                navArgument("bucketId") { type = NavType.LongType },
                navArgument("bucketName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bucketId = backStackEntry.arguments?.getLong("bucketId") ?: 0L
            val bucketName = backStackEntry.arguments?.getString("bucketName") ?: ""

            GalleryScreen(
                bucketId = bucketId,
                bucketName = bucketName,
                sharedViewModel = sharedViewModel,
                onMediaClick = { mediaItem ->
                    when (mediaItem.mediaType) {
                        com.aura.gallery.domain.model.MediaType.IMAGE -> {
                            navController.navigate(Screen.Viewer.createRoute(mediaItem.id))
                        }
                        com.aura.gallery.domain.model.MediaType.VIDEO -> {
                            navController.navigate(Screen.Player.createRoute(mediaItem.id))
                        }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Viewer.route,
            arguments = listOf(
                navArgument("mediaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L

            MediaViewerScreen(
                mediaId = mediaId,
                sharedViewModel = sharedViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("mediaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L

            VideoPlayerScreen(
                mediaId = mediaId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onMediaClick = { mediaItem ->
                    when (mediaItem.mediaType) {
                        com.aura.gallery.domain.model.MediaType.IMAGE -> {
                            navController.navigate(Screen.Viewer.createRoute(mediaItem.id))
                        }
                        com.aura.gallery.domain.model.MediaType.VIDEO -> {
                            navController.navigate(Screen.Player.createRoute(mediaItem.id))
                        }
                    }
                }
            )
        }
    }
}

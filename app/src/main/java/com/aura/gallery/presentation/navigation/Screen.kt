package com.aura.gallery.presentation.navigation

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    data object Albums : Screen("albums")
    data object Gallery : Screen("gallery/{bucketId}/{bucketName}") {
        fun createRoute(bucketId: Long, bucketName: String): String {
            return "gallery/$bucketId/$bucketName"
        }
    }
    data object Viewer : Screen("viewer/{mediaId}") {
        fun createRoute(mediaId: Long): String {
            return "viewer/$mediaId"
        }
    }
    data object Player : Screen("player/{mediaId}") {
        fun createRoute(mediaId: Long): String {
            return "player/$mediaId"
        }
    }
    data object Favorites : Screen("favorites")
    data object Trash : Screen("trash")
}

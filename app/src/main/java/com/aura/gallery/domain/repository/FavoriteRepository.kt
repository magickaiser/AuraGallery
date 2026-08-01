package com.aura.gallery.domain.repository

import com.aura.gallery.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing favorite media items.
 */
interface FavoriteRepository {

    /**
     * Returns all favorite media items as a Flow.
     */
    fun getFavorites(): Flow<List<MediaItem>>

    /**
     * Toggles the favorite status of a media item.
     */
    suspend fun toggleFavorite(mediaItem: MediaItem)

    /**
     * Checks if a media item is a favorite.
     */
    suspend fun isFavorite(mediaId: Long): Boolean
}

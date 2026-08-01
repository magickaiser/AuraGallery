package com.aura.gallery.domain.repository

import com.aura.gallery.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing trashed media items.
 */
interface TrashRepository {

    /**
     * Returns all items currently in the trash as a Flow.
     */
    fun getTrashedItems(): Flow<List<MediaItem>>

    /**
     * Moves a media item to the trash.
     */
    suspend fun moveToTrash(mediaItem: MediaItem)

    /**
     * Restores a media item from the trash.
     */
    suspend fun restoreFromTrash(mediaId: Long)

    /**
     * Permanently deletes all media items older than 30 days from the trash.
     */
    suspend fun cleanExpiredTrash()
}

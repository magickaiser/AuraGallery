package com.aura.gallery.domain.repository

import com.aura.gallery.domain.model.Album
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.model.MediaType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing device media via MediaStore.
 */
interface MediaRepository {

    /**
     * Returns all albums (buckets) on the device as a Flow.
     */
    fun getAlbums(): Flow<List<Album>>

    /**
     * Returns media items for a specific album.
     */
    fun getMediaByAlbum(bucketId: Long, filterType: MediaType? = null): Flow<List<MediaItem>>

    /**
     * Deletes a media item permanently from the device.
     * Requires write permission or scoped storage handling.
     */
    suspend fun deleteMedia(mediaItem: MediaItem): Boolean

    /**
     * Gets a single media item by its ID.
     */
    suspend fun getMediaById(mediaId: Long): MediaItem?
}

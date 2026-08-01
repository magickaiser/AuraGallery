package com.aura.gallery.domain.usecase

import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.repository.MediaRepository
import javax.inject.Inject

/**
 * Use case to share a media item via Android's share intent.
 * This wraps the platform-specific sharing logic.
 */
class ShareMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    /**
     * Returns the media item to be shared. The actual sharing
     * is handled by the presentation layer via Android intents.
     */
    suspend fun getMediaForSharing(mediaId: Long): MediaItem? {
        return mediaRepository.getMediaById(mediaId)
    }
}

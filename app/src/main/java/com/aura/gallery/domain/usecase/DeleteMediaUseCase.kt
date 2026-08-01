package com.aura.gallery.domain.usecase

import com.aura.gallery.domain.repository.MediaRepository
import com.aura.gallery.domain.model.MediaItem
import javax.inject.Inject

/**
 * Use case to delete a media item permanently from the device.
 */
class DeleteMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(mediaItem: MediaItem): Boolean {
        return mediaRepository.deleteMedia(mediaItem)
    }
}

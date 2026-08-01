package com.aura.gallery.domain.usecase

import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.model.MediaType
import com.aura.gallery.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve media items for a specific album with optional type filter.
 */
class GetMediaByAlbumUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(bucketId: Long, filterType: MediaType? = null): Flow<List<MediaItem>> {
        return mediaRepository.getMediaByAlbum(bucketId, filterType)
    }
}

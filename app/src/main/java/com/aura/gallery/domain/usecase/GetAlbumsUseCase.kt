package com.aura.gallery.domain.usecase

import com.aura.gallery.domain.model.Album
import com.aura.gallery.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all albums from the device.
 */
class GetAlbumsUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(): Flow<List<Album>> {
        return mediaRepository.getAlbums()
    }
}

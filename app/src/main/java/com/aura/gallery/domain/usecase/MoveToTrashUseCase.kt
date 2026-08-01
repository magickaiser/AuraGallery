package com.aura.gallery.domain.usecase

import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.repository.TrashRepository
import javax.inject.Inject

/**
 * Use case to move a media item to the trash.
 */
class MoveToTrashUseCase @Inject constructor(
    private val trashRepository: TrashRepository
) {
    suspend operator fun invoke(mediaItem: MediaItem) {
        trashRepository.moveToTrash(mediaItem)
    }
}

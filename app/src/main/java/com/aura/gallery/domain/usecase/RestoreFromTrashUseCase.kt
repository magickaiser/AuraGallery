package com.aura.gallery.domain.usecase

import com.aura.gallery.domain.repository.TrashRepository
import javax.inject.Inject

/**
 * Use case to restore a media item from the trash.
 */
class RestoreFromTrashUseCase @Inject constructor(
    private val trashRepository: TrashRepository
) {
    suspend operator fun invoke(mediaId: Long) {
        trashRepository.restoreFromTrash(mediaId)
    }
}

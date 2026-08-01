package com.aura.gallery.domain.usecase

import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.repository.TrashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all items in the trash.
 */
class GetTrashItemsUseCase @Inject constructor(
    private val trashRepository: TrashRepository
) {
    operator fun invoke(): Flow<List<MediaItem>> {
        return trashRepository.getTrashedItems()
    }
}

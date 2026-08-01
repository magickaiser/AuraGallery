package com.aura.gallery.domain.usecase

import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.repository.FavoriteRepository
import javax.inject.Inject

/**
 * Use case to toggle the favorite status of a media item.
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(mediaItem: MediaItem) {
        favoriteRepository.toggleFavorite(mediaItem)
    }
}

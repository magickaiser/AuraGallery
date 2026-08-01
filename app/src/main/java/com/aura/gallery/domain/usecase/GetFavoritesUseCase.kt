package com.aura.gallery.domain.usecase

import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all favorite media items.
 */
class GetFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<MediaItem>> {
        return favoriteRepository.getFavorites()
    }
}

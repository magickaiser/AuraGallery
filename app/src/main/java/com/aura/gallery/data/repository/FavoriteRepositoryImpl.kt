package com.aura.gallery.data.repository

import com.aura.gallery.data.local.db.dao.FavoriteDao
import com.aura.gallery.data.mapper.MediaMapper
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun getFavorites(): Flow<List<MediaItem>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { MediaMapper.toMediaItem(it) }
        }
    }

    override suspend fun toggleFavorite(mediaItem: MediaItem) {
        withContext(Dispatchers.IO) {
            if (favoriteDao.isFavorite(mediaItem.id)) {
                favoriteDao.removeFavorite(mediaItem.id)
            } else {
                favoriteDao.addFavorite(MediaMapper.toFavoriteEntity(mediaItem))
            }
        }
    }

    override suspend fun isFavorite(mediaId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            favoriteDao.isFavorite(mediaId)
        }
    }
}

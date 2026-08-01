package com.aura.gallery.data.repository

import com.aura.gallery.data.local.db.dao.TrashDao
import com.aura.gallery.data.mapper.MediaMapper
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.repository.TrashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrashRepositoryImpl @Inject constructor(
    private val trashDao: TrashDao
) : TrashRepository {

    companion object {
        private const val TRASH_RETENTION_MS = 30L * 24 * 60 * 60 * 1000 // 30 days
    }

    override fun getTrashedItems(): Flow<List<MediaItem>> {
        return trashDao.getAllTrashed().map { entities ->
            entities.map { MediaMapper.toMediaItem(it) }
        }
    }

    override suspend fun moveToTrash(mediaItem: MediaItem) {
        withContext(Dispatchers.IO) {
            trashDao.addToTrash(MediaMapper.toTrashEntity(mediaItem))
        }
    }

    override suspend fun restoreFromTrash(mediaId: Long) {
        withContext(Dispatchers.IO) {
            trashDao.removeFromTrash(mediaId)
        }
    }

    override suspend fun cleanExpiredTrash() {
        withContext(Dispatchers.IO) {
            val expirationTime = System.currentTimeMillis() - TRASH_RETENTION_MS
            trashDao.deleteExpired(expirationTime)
        }
    }
}

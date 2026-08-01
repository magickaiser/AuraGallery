package com.aura.gallery.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aura.gallery.data.local.db.entity.TrashEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrashDao {

    @Query("SELECT * FROM trash ORDER BY trashedAt DESC")
    fun getAllTrashed(): Flow<List<TrashEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToTrash(entity: TrashEntity)

    @Query("DELETE FROM trash WHERE mediaId = :mediaId")
    suspend fun removeFromTrash(mediaId: Long)

    @Query("DELETE FROM trash WHERE trashedAt < :expirationTime")
    suspend fun deleteExpired(expirationTime: Long)
}

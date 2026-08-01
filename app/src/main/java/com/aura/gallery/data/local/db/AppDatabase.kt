package com.aura.gallery.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aura.gallery.data.local.db.dao.FavoriteDao
import com.aura.gallery.data.local.db.entity.FavoriteEntity

@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
}

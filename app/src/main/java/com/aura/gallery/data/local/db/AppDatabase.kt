package com.aura.gallery.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aura.gallery.data.local.db.dao.FavoriteDao
import com.aura.gallery.data.local.db.dao.TrashDao
import com.aura.gallery.data.local.db.entity.FavoriteEntity
import com.aura.gallery.data.local.db.entity.TrashEntity

@Database(
    entities = [FavoriteEntity::class, TrashEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun trashDao(): TrashDao
}

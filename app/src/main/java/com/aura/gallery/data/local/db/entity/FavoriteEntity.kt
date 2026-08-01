package com.aura.gallery.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a favorited media item.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val mediaId: Long,
    val uri: String,
    val displayName: String,
    val mimeType: String,
    val size: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val width: Int,
    val height: Int,
    val bucketId: Long,
    val bucketName: String,
    val favoritedAt: Long = System.currentTimeMillis()
)

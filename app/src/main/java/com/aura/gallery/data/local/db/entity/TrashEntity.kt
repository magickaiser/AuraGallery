package com.aura.gallery.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a media item moved to the trash.
 */
@Entity(tableName = "trash")
data class TrashEntity(
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
    val trashedAt: Long = System.currentTimeMillis()
)

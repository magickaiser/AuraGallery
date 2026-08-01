package com.aura.gallery.data.mapper

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.aura.gallery.data.local.db.entity.FavoriteEntity
import com.aura.gallery.data.local.db.entity.TrashEntity
import com.aura.gallery.domain.model.Album
import com.aura.gallery.domain.model.MediaItem

/**
 * Maps between ContentResolver cursor rows, Room entities, and domain models.
 */
object MediaMapper {

    fun toMediaItem(cursor: android.database.Cursor): MediaItem {
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
        val dataCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
        } else {
            cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        }
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
        val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
        val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
        val dateModCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
        val widthCol = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH)
        val heightCol = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT)
        val bucketIdCol = cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_ID)
        val bucketNameCol = cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)

        val id = cursor.getLong(idCol)
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
        )

        return MediaItem(
            id = id,
            uri = contentUri.toString(),
            displayName = cursor.getString(nameCol) ?: "Unknown",
            mimeType = cursor.getString(mimeCol) ?: "image/*",
            size = cursor.getLong(sizeCol),
            dateAdded = cursor.getLong(dateAddedCol),
            dateModified = cursor.getLong(dateModCol),
            width = widthCol?.let { cursor.getInt(it) } ?: 0,
            height = heightCol?.let { cursor.getInt(it) } ?: 0,
            bucketId = bucketIdCol?.let { cursor.getLong(it) } ?: 0,
            bucketName = cursor.getString(bucketNameCol) ?: "Unknown"
        )
    }

    fun toFavoriteEntity(mediaItem: MediaItem): FavoriteEntity {
        return FavoriteEntity(
            mediaId = mediaItem.id,
            uri = mediaItem.uri,
            displayName = mediaItem.displayName,
            mimeType = mediaItem.mimeType,
            size = mediaItem.size,
            dateAdded = mediaItem.dateAdded,
            dateModified = mediaItem.dateModified,
            width = mediaItem.width,
            height = mediaItem.height,
            bucketId = mediaItem.bucketId,
            bucketName = mediaItem.bucketName
        )
    }

    fun toTrashEntity(mediaItem: MediaItem): TrashEntity {
        return TrashEntity(
            mediaId = mediaItem.id,
            uri = mediaItem.uri,
            displayName = mediaItem.displayName,
            mimeType = mediaItem.mimeType,
            size = mediaItem.size,
            dateAdded = mediaItem.dateAdded,
            dateModified = mediaItem.dateModified,
            width = mediaItem.width,
            height = mediaItem.height,
            bucketId = mediaItem.bucketId,
            bucketName = mediaItem.bucketName
        )
    }

    fun toMediaItem(entity: FavoriteEntity): MediaItem {
        return MediaItem(
            id = entity.mediaId,
            uri = entity.uri,
            displayName = entity.displayName,
            mimeType = entity.mimeType,
            size = entity.size,
            dateAdded = entity.dateAdded,
            dateModified = entity.dateModified,
            width = entity.width,
            height = entity.height,
            bucketId = entity.bucketId,
            bucketName = entity.bucketName,
            isFavorite = true
        )
    }

    fun toMediaItem(entity: TrashEntity): MediaItem {
        return MediaItem(
            id = entity.mediaId,
            uri = entity.uri,
            displayName = entity.displayName,
            mimeType = entity.mimeType,
            size = entity.size,
            dateAdded = entity.dateAdded,
            dateModified = entity.dateModified,
            width = entity.width,
            height = entity.height,
            bucketId = entity.bucketId,
            bucketName = entity.bucketName,
            isInTrash = true
        )
    }
}

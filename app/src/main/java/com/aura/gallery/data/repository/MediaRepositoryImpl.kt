package com.aura.gallery.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.aura.gallery.data.mapper.MediaMapper
import com.aura.gallery.domain.model.Album
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.model.MediaType
import com.aura.gallery.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaRepository {

    private val contentResolver: ContentResolver = context.contentResolver

    override fun getAlbums(): Flow<List<Album>> {
        val albums = loadAlbums()
        return MutableStateFlow(albums).asStateFlow()
    }

    override fun getMediaByAlbum(
        bucketId: Long,
        filterType: MediaType?
    ): Flow<List<MediaItem>> {
        val media = loadMedia(bucketId, filterType)
        return MutableStateFlow(media).asStateFlow()
    }

    override suspend fun getMediaById(mediaId: Long): MediaItem? {
        return withContext(Dispatchers.IO) {
            val uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaId
            )
            val projection = getMediaProjection()
            val selection = "${MediaStore.MediaColumns._ID} = ?"
            val selectionArgs = arrayOf(mediaId.toString())

            contentResolver.query(uri, projection, selection, selectionArgs, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        MediaMapper.toMediaItem(cursor)
                    } else null
                }
        }
    }

    override suspend fun deleteMedia(mediaItem: MediaItem): Boolean {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // On Android 11+, use MediaStore.createDeleteRequest
                val uris = mutableListOf<Uri>()
                uris.add(Uri.parse(mediaItem.uri))

                try {
                    val pendingIntent = MediaStore.createDeleteRequest(contentResolver, uris)
                    pendingIntent.send()
                    true
                } catch (e: Exception) {
                    false
                }
            } else {
                val rowsDeleted = contentResolver.delete(
                    Uri.parse(mediaItem.uri), null, null
                )
                rowsDeleted > 0
            }
        }
    }

    private fun loadAlbums(): List<Album> {
        val albumMap = mutableMapOf<Long, Album>()

        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_MODIFIED
        )

        contentResolver.query(
            imageUri,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dateModCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdCol)
                val bucketName = cursor.getString(bucketNameCol) ?: "Unknown"
                val imageId = cursor.getLong(idCol)
                val dateMod = cursor.getLong(dateModCol)

                val existing = albumMap[bucketId]
                if (existing == null) {
                    val coverUri = ContentUris.withAppendedId(imageUri, imageId).toString()
                    albumMap[bucketId] = Album(
                        bucketId = bucketId,
                        bucketName = bucketName,
                        coverUri = coverUri,
                        itemCount = 1,
                        photoCount = 1,
                        dateModified = dateMod
                    )
                } else {
                    albumMap[bucketId] = existing.copy(
                        itemCount = existing.itemCount + 1,
                        photoCount = existing.photoCount + 1,
                        dateModified = maxOf(existing.dateModified, dateMod)
                    )
                }
            }
        }

        return albumMap.values.toList()
    }

    private fun loadMedia(bucketId: Long, filterType: MediaType?): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()

        val projection = getMediaProjection()
        val selection = "${MediaStore.MediaColumns.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId.toString())

        when (filterType) {
            MediaType.IMAGE -> {
                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    "${MediaStore.MediaColumns.DATE_ADDED} DESC"
                )?.use { cursor ->
                    while (cursor.moveToNext()) {
                        mediaItems.add(MediaMapper.toMediaItem(cursor))
                    }
                }
            }
            MediaType.VIDEO -> {
                contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    "${MediaStore.MediaColumns.DATE_ADDED} DESC"
                )?.use { cursor ->
                    while (cursor.moveToNext()) {
                        mediaItems.add(MediaMapper.toMediaItem(cursor))
                    }
                }
            }
            null -> {
                // Load both images and videos
                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    "${MediaStore.MediaColumns.DATE_ADDED} DESC"
                )?.use { cursor ->
                    while (cursor.moveToNext()) {
                        mediaItems.add(MediaMapper.toMediaItem(cursor))
                    }
                }

                contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    "${MediaStore.MediaColumns.DATE_ADDED} DESC"
                )?.use { cursor ->
                    while (cursor.moveToNext()) {
                        mediaItems.add(MediaMapper.toMediaItem(cursor))
                    }
                }

                mediaItems.sortByDescending { it.dateAdded }
            }
        }

        return mediaItems
    }

    private fun getMediaProjection(): Array<String> {
        return arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.RELATIVE_PATH
        )
    }
}

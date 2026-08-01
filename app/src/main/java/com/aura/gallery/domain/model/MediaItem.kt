package com.aura.gallery.domain.model

/**
 * Domain model representing a single media item (image or video).
 */
data class MediaItem(
    val id: Long,
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
    val isFavorite: Boolean = false,
    val isInTrash: Boolean = false
) {
    val mediaType: MediaType
        get() = MediaType.fromMimeType(mimeType)

    val formattedSize: String
        get() {
            val kb = size / 1024
            return if (kb < 1024) "$kb KB"
            else "${"%.1f".format(kb / 1024.0)} MB"
        }
}

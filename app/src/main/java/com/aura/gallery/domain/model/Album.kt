package com.aura.gallery.domain.model

/**
 * Domain model representing an album (folder/bucket) of media items.
 */
data class Album(
    val bucketId: Long,
    val bucketName: String,
    val coverUri: String?,
    val itemCount: Int,
    val photoCount: Int = 0,
    val videoCount: Int = 0,
    val dateModified: Long = 0
)

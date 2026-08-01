package com.aura.gallery.domain.model

/**
 * Represents the type of media item.
 */
enum class MediaType {
    IMAGE,
    VIDEO;

    companion object {
        fun fromMimeType(mimeType: String?): MediaType {
            return when {
                mimeType?.startsWith("image/") == true -> IMAGE
                mimeType?.startsWith("video/") == true -> VIDEO
                else -> IMAGE
            }
        }
    }
}

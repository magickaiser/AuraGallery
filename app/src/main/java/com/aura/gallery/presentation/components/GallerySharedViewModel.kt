package com.aura.gallery.presentation.components

import androidx.lifecycle.ViewModel
import com.aura.gallery.domain.model.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Shared ViewModel that holds the current media list and index,
 * enabling swipe navigation between images.
 */
@HiltViewModel
class GallerySharedViewModel @Inject constructor() : ViewModel() {

    private var mediaList: List<MediaItem> = emptyList()
    private var currentIndex: Int = -1

    fun setMediaList(list: List<MediaItem>, clickedItemId: Long) {
        mediaList = list
        currentIndex = list.indexOfFirst { it.id == clickedItemId }.coerceAtLeast(0)
    }

    fun getCurrentMedia(): MediaItem? {
        return if (currentIndex in mediaList.indices) mediaList[currentIndex] else null
    }

    fun getCurrentIndex(): Int = currentIndex

    fun getTotalCount(): Int = mediaList.size

    fun hasNext(): Boolean = currentIndex < mediaList.lastIndex

    fun hasPrevious(): Boolean = currentIndex > 0

    fun goNext(): MediaItem? {
        if (!hasNext()) return null
        currentIndex++
        return mediaList[currentIndex]
    }

    fun goPrevious(): MediaItem? {
        if (!hasPrevious()) return null
        currentIndex--
        return mediaList[currentIndex]
    }

    fun clear() {
        mediaList = emptyList()
        currentIndex = -1
    }
}

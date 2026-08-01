package com.aura.gallery.presentation.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.model.MediaType
import com.aura.gallery.domain.usecase.GetMediaByAlbumUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GalleryUiState(
    val mediaItems: List<MediaItem> = emptyList(),
    val filteredItems: List<MediaItem> = emptyList(),
    val bucketName: String = "",
    val currentFilter: MediaType? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMediaByAlbumUseCase: GetMediaByAlbumUseCase
) : ViewModel() {

    private val bucketId: Long = savedStateHandle.get<Long>("bucketId") ?: 0L

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        loadMedia()
    }

    fun setFilter(filter: MediaType?) {
        _uiState.value = _uiState.value.copy(currentFilter = filter)
        applyFilter()
    }

    private fun loadMedia() {
        viewModelScope.launch {
            getMediaByAlbumUseCase(bucketId).collect { items ->
                _uiState.value = _uiState.value.copy(
                    mediaItems = items,
                    isLoading = false
                )
                applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val state = _uiState.value
        val filtered = if (state.currentFilter != null) {
            state.mediaItems.filter { it.mediaType == state.currentFilter }
        } else {
            state.mediaItems
        }
        _uiState.value = state.copy(filteredItems = filtered)
    }
}

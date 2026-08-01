package com.aura.gallery.presentation.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.model.MediaType
import com.aura.gallery.domain.usecase.DeleteMediaUseCase
import com.aura.gallery.domain.usecase.GetMediaByAlbumUseCase
import com.aura.gallery.domain.usecase.ToggleFavoriteUseCase
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
    val isLoading: Boolean = true,
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet()
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMediaByAlbumUseCase: GetMediaByAlbumUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase
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

    // --- Selection mode ---

    fun enterSelectionMode(mediaId: Long) {
        _uiState.value = _uiState.value.copy(
            isSelectionMode = true,
            selectedIds = setOf(mediaId)
        )
    }

    fun toggleSelection(mediaId: Long) {
        val state = _uiState.value
        val newIds = if (mediaId in state.selectedIds) {
            state.selectedIds - mediaId
        } else {
            state.selectedIds + mediaId
        }
        _uiState.value = state.copy(
            selectedIds = newIds,
            isSelectionMode = newIds.isNotEmpty()
        )
    }

    fun exitSelectionMode() {
        _uiState.value = _uiState.value.copy(
            isSelectionMode = false,
            selectedIds = emptySet()
        )
    }

    fun selectAll() {
        _uiState.value = _uiState.value.copy(
            selectedIds = _uiState.value.filteredItems.map { it.id }.toSet()
        )
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val selectedMedia = _uiState.value.filteredItems
                .filter { it.id in _uiState.value.selectedIds }
            selectedMedia.forEach { deleteMediaUseCase(it) }
            exitSelectionMode()
        }
    }

    fun favoriteSelected() {
        viewModelScope.launch {
            val selectedMedia = _uiState.value.filteredItems
                .filter { it.id in _uiState.value.selectedIds }
            selectedMedia.forEach { toggleFavoriteUseCase(it) }
            exitSelectionMode()
        }
    }

    // --- Data loading ---

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

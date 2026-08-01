package com.aura.gallery.presentation.viewer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.repository.FavoriteRepository
import com.aura.gallery.domain.repository.MediaRepository
import com.aura.gallery.domain.usecase.DeleteMediaUseCase
import com.aura.gallery.domain.usecase.MoveToTrashUseCase
import com.aura.gallery.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaViewerUiState(
    val mediaItem: MediaItem? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class MediaViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
    private val favoriteRepository: FavoriteRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val moveToTrashUseCase: MoveToTrashUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase
) : ViewModel() {

    private val mediaId: Long = savedStateHandle.get<Long>("mediaId") ?: 0L

    private val _uiState = MutableStateFlow(MediaViewerUiState())
    val uiState: StateFlow<MediaViewerUiState> = _uiState.asStateFlow()

    init {
        loadMedia()
    }

    private fun loadMedia() {
        viewModelScope.launch {
            val item = mediaRepository.getMediaById(mediaId)
            val fav = favoriteRepository.isFavorite(mediaId)
            _uiState.value = MediaViewerUiState(
                mediaItem = item,
                isFavorite = fav,
                isLoading = false
            )
        }
    }

    fun loadMediaById(id: Long) {
        viewModelScope.launch {
            _uiState.value = MediaViewerUiState(isLoading = true)
            val item = mediaRepository.getMediaById(id)
            val fav = favoriteRepository.isFavorite(id)
            _uiState.value = MediaViewerUiState(
                mediaItem = item,
                isFavorite = fav,
                isLoading = false
            )
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.mediaItem?.let { item ->
                toggleFavoriteUseCase(item)
                _uiState.value = _uiState.value.copy(
                    isFavorite = !_uiState.value.isFavorite
                )
            }
        }
    }

    fun moveToTrash() {
        viewModelScope.launch {
            _uiState.value.mediaItem?.let { item ->
                moveToTrashUseCase(item)
            }
        }
    }

    fun deletePermanently() {
        viewModelScope.launch {
            _uiState.value.mediaItem?.let { item ->
                deleteMediaUseCase(item)
            }
        }
    }
}

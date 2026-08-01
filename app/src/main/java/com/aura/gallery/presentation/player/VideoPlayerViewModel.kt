package com.aura.gallery.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class VideoPlayerUiState(
    val mediaItem: MediaItem? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val mediaId: Long = savedStateHandle.get<Long>("mediaId") ?: 0L

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    init {
        // Load is handled by the screen directly
        _uiState.value = VideoPlayerUiState(isLoading = false)
    }

    suspend fun loadMedia(): MediaItem? {
        return mediaRepository.getMediaById(mediaId)
    }
}

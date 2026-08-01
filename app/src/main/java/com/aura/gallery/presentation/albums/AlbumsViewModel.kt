package com.aura.gallery.presentation.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.gallery.domain.model.Album
import com.aura.gallery.domain.usecase.GetAlbumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlbumsUiState(
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = true,
    val hasPermissions: Boolean = false
)

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val getAlbumsUseCase: GetAlbumsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumsUiState())
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    fun setPermissionsGranted(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasPermissions = granted)
        if (granted && _uiState.value.albums.isEmpty()) {
            loadAlbums()
        }
    }

    fun retryLoad() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadAlbums()
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            getAlbumsUseCase().collect { albums ->
                // Prepend special "All photos" album
                val allPhotosAlbum = Album(
                    bucketId = 0L,
                    bucketName = "Todas las fotos",
                    coverUri = albums.firstOrNull()?.coverUri,
                    itemCount = albums.sumOf { it.itemCount },
                    photoCount = albums.sumOf { it.photoCount },
                    videoCount = albums.sumOf { it.videoCount },
                    dateModified = albums.maxOfOrNull { it.dateModified } ?: 0
                )

                _uiState.value = AlbumsUiState(
                    albums = listOf(allPhotosAlbum) + albums,
                    isLoading = false,
                    hasPermissions = albums.isNotEmpty()
                )
            }
        }
    }
}

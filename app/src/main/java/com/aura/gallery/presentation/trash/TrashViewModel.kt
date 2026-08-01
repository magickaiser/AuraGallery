package com.aura.gallery.presentation.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.usecase.DeleteMediaUseCase
import com.aura.gallery.domain.usecase.GetTrashItemsUseCase
import com.aura.gallery.domain.usecase.RestoreFromTrashUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrashUiState(
    val trashedItems: List<MediaItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val getTrashItemsUseCase: GetTrashItemsUseCase,
    private val restoreFromTrashUseCase: RestoreFromTrashUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrashUiState())
    val uiState: StateFlow<TrashUiState> = _uiState.asStateFlow()

    init {
        loadTrash()
    }

    private fun loadTrash() {
        viewModelScope.launch {
            getTrashItemsUseCase().collect { items ->
                _uiState.value = TrashUiState(
                    trashedItems = items,
                    isLoading = false
                )
            }
        }
    }

    fun restoreItem(mediaId: Long) {
        viewModelScope.launch {
            restoreFromTrashUseCase(mediaId)
        }
    }

    fun deletePermanently(mediaItem: MediaItem) {
        viewModelScope.launch {
            deleteMediaUseCase(mediaItem)
        }
    }
}

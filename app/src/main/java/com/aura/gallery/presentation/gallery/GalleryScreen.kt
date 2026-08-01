package com.aura.gallery.presentation.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.presentation.components.EmptyStateView
import com.aura.gallery.presentation.components.GallerySharedViewModel
import com.aura.gallery.presentation.components.MediaThumbnail
import com.aura.gallery.presentation.components.MediaTypeFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    bucketId: Long,
    bucketName: String,
    onMediaClick: (MediaItem) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: GalleryViewModel = hiltViewModel(),
    sharedViewModel: GallerySharedViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            if (uiState.isSelectionMode) {
                TopAppBar(
                    title = { Text("${uiState.selectedIds.size} seleccionadas") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.exitSelectionMode() }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cancelar selección"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll() }) {
                            Icon(
                                imageVector = Icons.Filled.SelectAll,
                                contentDescription = "Seleccionar todo"
                            )
                        }
                        IconButton(onClick = { viewModel.favoriteSelected() }) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Añadir a favoritos"
                            )
                        }
                        IconButton(onClick = { viewModel.trashSelected() }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Mover a papelera"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            } else {
                TopAppBar(
                    title = { Text(bucketName) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            if (!uiState.isSelectionMode) {
                MediaTypeFilter(
                    currentFilter = uiState.currentFilter,
                    onFilterSelected = { viewModel.setFilter(it) }
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredItems.isEmpty()) {
                EmptyStateView(
                    message = "No se encontraron archivos",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = uiState.filteredItems,
                        key = { it.id }
                    ) { mediaItem ->
                        MediaThumbnail(
                            mediaItem = mediaItem,
                            isSelected = mediaItem.id in uiState.selectedIds,
                            isSelectionMode = uiState.isSelectionMode,
                            onClick = {
                                if (uiState.isSelectionMode) {
                                    viewModel.toggleSelection(mediaItem.id)
                                } else {
                                    sharedViewModel.setMediaList(
                                        uiState.filteredItems, mediaItem.id
                                    )
                                    onMediaClick(mediaItem)
                                }
                            },
                            onLongClick = if (!uiState.isSelectionMode) {
                                { viewModel.enterSelectionMode(mediaItem.id) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

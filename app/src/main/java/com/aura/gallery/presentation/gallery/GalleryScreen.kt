package com.aura.gallery.presentation.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.aura.gallery.presentation.components.MediaThumbnail
import com.aura.gallery.presentation.components.MediaTypeFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    bucketId: Long,
    bucketName: String,
    onMediaClick: (MediaItem) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
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
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MediaTypeFilter(
                currentFilter = uiState.currentFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                )
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
                            onClick = { onMediaClick(mediaItem) }
                        )
                    }
                }
            }
        }
    }
}

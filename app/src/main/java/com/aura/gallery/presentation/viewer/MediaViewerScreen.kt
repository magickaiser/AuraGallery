package com.aura.gallery.presentation.viewer

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.presentation.components.EmptyStateView
import com.aura.gallery.presentation.components.GallerySharedViewModel
import com.aura.gallery.presentation.theme.FavoriteColor
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaViewerScreen(
    mediaId: Long,
    onNavigateBack: () -> Unit,
    viewModel: MediaViewerViewModel = hiltViewModel(),
    sharedViewModel: GallerySharedViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var currentMediaId by remember { mutableLongStateOf(mediaId) }

    LaunchedEffect(currentMediaId) {
        viewModel.loadMediaById(currentMediaId)
    }

    val currentIndex = sharedViewModel.getCurrentIndex()
    val totalCount = sharedViewModel.getTotalCount()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (totalCount > 1) {
                        Text(
                            text = "${currentIndex + 1} / $totalCount",
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite)
                                Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (uiState.isFavorite) FavoriteColor else Color.White
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = uiState.mediaItem?.mimeType ?: "image/*"
                            putExtra(Intent.EXTRA_STREAM, Uri.parse(uiState.mediaItem?.uri))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir"))
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Compartir",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        viewModel.deletePermanently()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Mover a papelera",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading && uiState.mediaItem == null) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color.White
                )
            } else if (uiState.mediaItem != null) {
                AnimatedContent(
                    targetState = currentMediaId,
                    transitionSpec = {
                        val dir = if (targetState > initialState)
                            AnimatedContentTransitionScope.SlideDirection.Left
                        else
                            AnimatedContentTransitionScope.SlideDirection.Right
                        slideIntoContainer(dir, tween(300)) togetherWith
                                slideOutOfContainer(dir, tween(300)) using
                                SizeTransform(clip = false)
                    },
                    label = "image_swipe"
                ) { targetId ->
                    key(targetId) {
                        SwipeableZoomImage(
                            mediaItem = uiState.mediaItem!!,
                            onSwipeNext = {
                                sharedViewModel.goNext()?.let { currentMediaId = it.id }
                            },
                            onSwipePrevious = {
                                sharedViewModel.goPrevious()?.let { currentMediaId = it.id }
                            },
                            hasNext = sharedViewModel.hasNext(),
                            hasPrevious = sharedViewModel.hasPrevious()
                        )
                    }
                }
            } else {
                EmptyStateView(message = "Imagen no disponible")
            }
        }
    }
}

@Composable
private fun SwipeableZoomImage(
    mediaItem: MediaItem,
    onSwipeNext: () -> Unit,
    onSwipePrevious: () -> Unit,
    hasNext: Boolean,
    hasPrevious: Boolean
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val currentScale by rememberUpdatedState(scale)
    val currentContainerSize by rememberUpdatedState(containerSize)

    // Horizontal drag offset for visual feedback during swipe
    var dragOffsetX by remember { mutableFloatStateOf(0f) }

    AsyncImage(
        model = mediaItem.uri,
        contentDescription = mediaItem.displayName,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX + dragOffsetX,
                translationY = offsetY
            )
            .onSizeChanged { containerSize = it }
            // Transform gestures (pinch zoom + two-finger pan)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (currentScale * zoom).coerceIn(1f, 5f)
                    scale = newScale

                    if (newScale > 1f && currentContainerSize != IntSize.Zero) {
                        val maxX = (currentContainerSize.width * (newScale - 1f)) / 2f
                        val maxY = (currentContainerSize.height * (newScale - 1f)) / 2f
                        offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                        offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            }
            // Swipe gesture (only when not zoomed)
            .pointerInput(currentScale > 1f) {
                if (currentScale <= 1f) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffsetX > 150f && hasPrevious) {
                                onSwipePrevious()
                            } else if (dragOffsetX < -150f && hasNext) {
                                onSwipeNext()
                            }
                            dragOffsetX = 0f
                        },
                        onHorizontalDrag = { _, amount ->
                            if (currentScale <= 1f) {
                                // Visual drag offset (clamped)
                                dragOffsetX = (dragOffsetX + amount)
                                    .coerceIn(-200f, 200f)
                            }
                        },
                        onDragCancel = { dragOffsetX = 0f }
                    )
                }
            }
            // Tap/double-tap
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > 1f) 1f else 2.5f
                        if (scale == 1f) {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    }
                )
            },
        contentScale = ContentScale.Fit
    )
}

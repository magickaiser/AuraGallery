package com.aura.gallery.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aura.gallery.domain.model.MediaItem
import com.aura.gallery.domain.model.MediaType
import com.aura.gallery.presentation.theme.VideoIndicatorColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaThumbnail(
    mediaItem: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onLongClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .then(
                if (onLongClick != null) {
                    Modifier.combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                } else {
                    Modifier.combinedClickable(onClick = onClick)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = mediaItem.uri,
            contentDescription = mediaItem.displayName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Selection overlay
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.25f)
                        else Color.Black.copy(alpha = 0.25f)
                    )
            )
        }

        // Checkmark indicator in selection mode
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .then(
                        if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                        else Modifier.border(
                            2.dp,
                            Color.White, CircleShape
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Seleccionado",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Video indicator
        if (mediaItem.mediaType == MediaType.VIDEO && !isSelectionMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(28.dp)
                    .clip(RoundedCornerShape(50))
                    .background(VideoIndicatorColor.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Video",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

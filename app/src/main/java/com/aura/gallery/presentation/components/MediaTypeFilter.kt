package com.aura.gallery.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aura.gallery.domain.model.MediaType

@Composable
fun MediaTypeFilter(
    currentFilter: MediaType?,
    onFilterSelected: (MediaType?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter == null,
            onClick = { onFilterSelected(null) },
            label = { Text("Todos") }
        )
        FilterChip(
            selected = currentFilter == MediaType.IMAGE,
            onClick = { onFilterSelected(MediaType.IMAGE) },
            label = { Text("Fotos") }
        )
        FilterChip(
            selected = currentFilter == MediaType.VIDEO,
            onClick = { onFilterSelected(MediaType.VIDEO) },
            label = { Text("Videos") }
        )
    }
}

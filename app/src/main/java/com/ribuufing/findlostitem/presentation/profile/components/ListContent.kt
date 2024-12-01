package com.ribuufing.findlostitem.presentation.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ribuufing.findlostitem.data.model.LostItem

@Composable
fun ListContent(foundItems: List<LostItem>) {
    var selectedItem by remember { mutableStateOf<LostItem?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = 8.dp,
            bottom = 80.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(foundItems) { item ->
            GridItemCard(
                item = item,
                onItemClick = { selectedItem = item }
            )
        }
    }

    if (selectedItem != null) {
        ItemBottomSheet(
            item = selectedItem!!,
            onDismiss = { selectedItem = null }
        )
    }
} 
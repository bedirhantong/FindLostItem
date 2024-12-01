package com.ribuufing.findlostitem.presentation.reportfounditem.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImageSection(
    selectedImages: List<Uri>,
    onAddImage: () -> Unit,
    onRemoveImage: (Uri) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFF2EDE8))
    ) {
        if (selectedImages.isEmpty()) {
            EmptyImageState(onAddImage)
        } else {
            ImageGrid(
                selectedImages = selectedImages,
                onAddImage = onAddImage,
                onRemoveImage = onRemoveImage
            )
        }
    }
}

@Composable
private fun EmptyImageState(onAddImage: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onAddImage),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Photos",
            modifier = Modifier.size(48.dp),
            tint = Color(0xFFED822B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add Photos",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFED822B)
        )
    }
}

@Composable
private fun ImageGrid(
    selectedImages: List<Uri>,
    onAddImage: () -> Unit,
    onRemoveImage: (Uri) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(selectedImages) { uri ->
            ImageItem(uri = uri, onRemove = { onRemoveImage(uri) })
        }
        item {
            AddImageButton(onAddImage)
        }
    }
}

@Composable
private fun ImageItem(uri: Uri, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
    ) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AddImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFED822B).copy(alpha = 0.1f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add more",
            tint = Color(0xFFED822B)
        )
    }
} 
package com.ribuufing.findlostitem.presentation.reportfounditem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun LocationField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: Painter,
    hasLocation: Boolean,
    onLocationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFFED822B)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFED822B),
                focusedLabelColor = Color(0xFFED822B),
                cursorColor = Color(0xFFED822B)
            ),
            singleLine = true
        )
        IconButton(
            onClick = onLocationClick,
            modifier = Modifier
                .background(
                    if (hasLocation) Color(0xFFED822B) else Color.Gray,
                    CircleShape
                )
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Pick location",
                tint = Color.White
            )
        }
    }
} 
package com.ribuufing.findlostitem.presentation.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ribuufing.findlostitem.R

@Composable
fun MessageInputBar(onMessageSent: (String) -> Unit) {
    var messageContent by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = messageContent,
            onValueChange = { messageContent = it },
            placeholder = {
                Text(
                    text = "Type your message...",
                )
            },
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent)
                .padding(end = 8.dp),
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.Blue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
        IconButton(
            onClick = {
                if (messageContent.isNotBlank()) {
                    onMessageSent(messageContent)
                    messageContent = ""
                }
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send),
                contentDescription = "Send Message",
                tint = Color(0xFF007AFF)
            )
        }
    }
}
package com.ribuufing.findlostitem.presentation.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.presentation.screens.chat.UserImage

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.senderUser.id == 1) Arrangement.Start else Arrangement.End
    ) {
        if (message.senderUser.id == 1) {
            UserImage(message.senderUser.imageUrl)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(
            horizontalAlignment = if (message.senderUser.id == 1) Alignment.Start else Alignment.End
        ) {
            Text(
                text = message.senderUser.name,
                fontWeight = FontWeight.Thin,
                fontSize = 14.sp,
                color = Color(0xFF61828A)
            )
            Box(
                modifier = Modifier
                    .background(
                        color = if (message.senderUser.id == 1) Color(0xFFF0F5F5) else Color(
                            0xFFED822B
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = message.content, fontSize = 16.sp,
                    color = if (message.senderUser.id == 1) Color.Black else Color.White,
                )
            }
            Text(
                text = message.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        if (message.senderUser.id != 1) {
            Spacer(modifier = Modifier.width(8.dp))
            UserImage(message.senderUser.imageUrl)
        }
    }
}
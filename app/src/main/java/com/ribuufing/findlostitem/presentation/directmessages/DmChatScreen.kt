package com.ribuufing.findlostitem.presentation.directmessages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Message

@Composable
fun DmChatScreen(
    navController: NavHostController,
    viewModel: DmChatViewModel = hiltViewModel()
) {
    // Veri akışını izleme
    val chats by viewModel.chats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState(initial = false)

    // Ekranda yükleme göstergesi varsa göster
    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(chats) { chat ->
                ChatItem(chat = chat) {
                    // Chat'e tıklayınca detay sayfasına git
                    navController.navigate("chatDetailScreen/${chat.id}")
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(text = "Chat with ${chat.participants.joinToString(", ")}", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = chat.lastMessage)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Last message at ${chat.lastMessageTimestamp.toDate()}")
    }
}




@Composable
fun ChatDetailScreen(
    navController: NavHostController,
    chatId: String,
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    // Mesajlar ve yükleme durumunu izleme
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val currentUserId by viewModel.currentUserId.collectAsState()

    // Ekranda yükleme göstergesi varsa göster
    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                items(messages) { message ->
                    MessageItem(message = message, currentUserId = currentUserId)
                }
            }

            // Mesaj yazma alanı
            var messageText by remember { mutableStateOf("") }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                BasicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    viewModel.sendMessage(chatId, messageText)
                    messageText = ""
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, currentUserId: String) {
    val isSentByCurrentUser = message.senderId == currentUserId
    val alignment = if (isSentByCurrentUser) Alignment.End else Alignment.Start
    val backgroundColor = if (isSentByCurrentUser) Color(0xFFED822B) else Color.Gray

    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

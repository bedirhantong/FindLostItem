package com.ribuufing.findlostitem.presentation.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.utils.Result
import androidx.navigation.NavHostController
import com.ribuufing.findlostitem.data.model.Chat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    itemId: String,
    senderUid: String,
    receiverUid: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    // Chat oluşturma veya alma işlemini başlat
    LaunchedEffect(itemId, senderUid, receiverUid) {
        viewModel.createOrGetChat(itemId, senderUid, receiverUid)
    }

    // ViewModel'deki durumları gözlemle
    val chatState by viewModel.chatState.collectAsState()
    val messagesState by viewModel.messagesState.collectAsState()
    var messageText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (chatState) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Success -> {
                    val chat = (chatState as Result.Success<Chat>).data
                    if (chat != null) {
                        when (messagesState) {
                            is Result.Loading -> {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is Result.Success -> {
                                val messages = (messagesState as Result.Success<List<Message>>).data
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    reverseLayout = true
                                ) {
                                    items(messages) { message ->
                                        MessageItem(message, message.senderId == senderUid)
                                    }
                                }
                            }
                            is Result.Failure -> {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Error loading messages: ${(messagesState as Result.Failure).exception.message}")
                                }
                            }
                        }

                        // Mesaj gönderme kutusu
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Type a message") }
                            )
                            IconButton(
                                onClick = {
                                    if (messageText.isNotBlank()) {
                                        viewModel.sendMessage(senderUid, messageText)
                                        messageText = ""
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send")
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No chat found")
                        }
                    }
                }
                is Result.Failure -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: ${(chatState as Result.Failure).exception.message}")
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, isCurrentUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(8.dp),
                color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}





//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SendMessageBar(
//    chatViewModel: ChatViewModel,
//    receiverUid: String
//) {
//    var messageText by remember { mutableStateOf("") }
//    val senderUid = chatViewModel.getCurrentUserUid()
//    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//    val focusRequester = remember { FocusRequester() }
//
//    Surface(
//        modifier = Modifier.fillMaxWidth(),
//        color = MaterialTheme.colorScheme.background,
//        tonalElevation = 2.dp
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 8.dp),
//            verticalAlignment = Alignment.Bottom
//        ) {
//            TextField(
//                value = messageText,
//                onValueChange = { messageText = it },
//                modifier = Modifier
//                    .weight(1f)
//                    .focusRequester(focusRequester),
//                placeholder = { Text("Type a message...") },
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
//                keyboardActions = KeyboardActions(onSend = {
//                    if (messageText.isNotBlank()) {
//                        sendMessage(chatViewModel, senderUid, receiverUid, messageText, date)
//                        messageText = ""
//                    }
//                }),
//                colors = TextFieldDefaults.textFieldColors(
//                    containerColor = Color.Transparent,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                ),
//                maxLines = 5
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            IconButton(
//                onClick = {
//                    if (messageText.isNotBlank()) {
//                        sendMessage(chatViewModel, senderUid, receiverUid, messageText, date)
//                        messageText = ""
//                        focusRequester.requestFocus()
//                    }
//                },
//                modifier = Modifier.align(Alignment.Bottom)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Send,
//                    contentDescription = "Send",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//    }
//}
//
//private fun sendMessage(
//    chatViewModel: ChatViewModel,
//    senderUid: String,
//    receiverUid: String,
//    content: String,
//    date: String
//) {
//    val newMessage = Message(
//        content = content,
//        user1Id = senderUid,
//        user2Id = receiverUid,
//        id = "",  // This will be set by Firestore
//        date = date
//    )
//    chatViewModel.sendMessage(newMessage)
//}
//
//
//@Composable
//fun MessageItem(message: Message, senderUserInfos: Result<User?>) {
//    val isSender = senderUserInfos is Result.Success && message.user1Id == senderUserInfos.data?.uid
//    val bubbleColor = if (isSender) {
//        MaterialTheme.colorScheme.primaryContainer
//    } else {
//        MaterialTheme.colorScheme.secondaryContainer
//    }
//    val textColor = if (isSender) {
//        MaterialTheme.colorScheme.onPrimaryContainer
//    } else {
//        MaterialTheme.colorScheme.onSecondaryContainer
//    }
//    val alignment = if (isSender) Alignment.End else Alignment.Start
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        horizontalAlignment = alignment
//    ) {
//        Surface(
//            shape = RoundedCornerShape(16.dp),
//            color = bubbleColor,
//            modifier = Modifier
//                .clip(RoundedCornerShape(16.dp))
//                .widthIn(max = 280.dp)
//                .animateContentSize()
//        ) {
//            Column(modifier = Modifier.padding(12.dp)) {
//                Text(
//                    text = message.content,
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = textColor
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = formatMessageDate(message.date),
//                    style = MaterialTheme.typography.bodySmall,
//                    color = textColor.copy(alpha = 0.7f),
//                    modifier = Modifier.align(Alignment.End)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun formatMessageDate(dateString: String): String {
//    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//    val date = inputFormat.parse(dateString) ?: return dateString
//
//    val calendar = Calendar.getInstance()
//    val today = calendar.time
//    calendar.add(Calendar.DAY_OF_YEAR, -1)
//    val yesterday = calendar.time
//
//    return when {
//        isSameDay(date, today) -> "Today ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)}"
//        isSameDay(date, yesterday) -> "Yesterday ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)}"
//        else -> SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(date)
//    }
//}
//
//fun isSameDay(date1: Date, date2: Date): Boolean {
//    val cal1 = Calendar.getInstance().apply { time = date1 }
//    val cal2 = Calendar.getInstance().apply { time = date2 }
//    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
//}
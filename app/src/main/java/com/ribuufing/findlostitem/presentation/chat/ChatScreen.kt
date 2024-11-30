package com.ribuufing.findlostitem.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.utils.Result
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.LostItem
import java.text.SimpleDateFormat
import java.util.Locale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    itemId: String,
    senderUid: String,
    receiverUid: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    LaunchedEffect(itemId, senderUid, receiverUid) {
        viewModel.createOrGetChat(itemId, senderUid, receiverUid)
    }

    val chatState by viewModel.chatState.collectAsState()
    val messagesState by viewModel.messagesState.collectAsState()
    val lostItemState by viewModel.lostItemState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var isItemDetailsVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFED822B))
            ) {
                CenterAlignedTopAppBar(
                    title = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            when (lostItemState) {
                                is Result.Success -> {
                                    val item = (lostItemState as Result.Success<LostItem>).data
                                    if (item.images.isNotEmpty()) {
                                        AsyncImage(
                                            model = item.images.first(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(
                                        text = item.title,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                else -> Text("Chat", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isItemDetailsVisible = !isItemDetailsVisible }) {
                            Icon(
                                imageVector = if (isItemDetailsVisible) Icons.Default.KeyboardArrowUp 
                                            else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle item details",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFED822B)
                    )
                )

                AnimatedVisibility(
                    visible = isItemDetailsVisible,
                    enter = slideInVertically() + expandVertically(),
                    exit = slideOutVertically() + shrinkVertically()
                ) {
                    when (lostItemState) {
                        is Result.Success -> {
                            val item = (lostItemState as Result.Success<LostItem>).data
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Color(0xFFED822B)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    if (item.images.isNotEmpty()) {
                                        AsyncImage(
                                            model = item.images.first(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    Text(
                                        text = item.description,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Found: ${if (item.isFound) "Yes" else "No"}",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Date: ${item.date}",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        is Result.Loading -> {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        is Result.Failure -> {
                            Text(
                                text = "Error loading item details",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (chatState) {
                is Result.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Result.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (messagesState) {
                            is Result.Success -> {
                                val messages = (messagesState as Result.Success<List<Message>>).data
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    reverseLayout = true,
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(messages) { message ->
                                        MessageItem(
                                            message = message,
                                            isCurrentUser = message.senderId == senderUid,
                                            userImage = viewModel.getUserImage(message.senderId)
                                        )
                                    }
                                }
                            }
                            is Result.Loading -> {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is Result.Failure -> {
                                Text(
                                    text = "Error loading messages",
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(24.dp),
                            shadowElevation = 4.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = messageText,
                                    onValueChange = { messageText = it },
                                    modifier = Modifier.weight(1f),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    placeholder = { Text("Type a message...") }
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                IconButton(
                                    onClick = {
                                        if (messageText.isNotBlank()) {
                                            viewModel.sendMessage(senderUid, messageText)
                                            messageText = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFFED822B))
                                        .size(40.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                is Result.Failure -> {
                    Text(
                        text = "Error loading chat",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    isCurrentUser: Boolean,
    userImage: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isCurrentUser) {
            AsyncImage(
                model = userImage ?: "https://cdn.pixabay.com/photo/2014/03/25/16/24/female-296989_1280.png",
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart =  if (isCurrentUser) 20.dp else 4.dp,
                    topEnd = if (isCurrentUser) 4.dp else 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                ),
                color = if (isCurrentUser) Color(0xFFED822B) else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.widthIn(max = 340.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = message.content,
                        color = if (isCurrentUser) Color.White 
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Text(
                        text = message.timestamp.formatToReadableTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCurrentUser) Color.White.copy(alpha = 0.7f) 
                               else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }

        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = userImage ?: "https://cdn.pixabay.com/photo/2014/03/25/16/24/female-296989_1280.png",
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private fun Timestamp.formatToReadableTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(this.toDate())
}
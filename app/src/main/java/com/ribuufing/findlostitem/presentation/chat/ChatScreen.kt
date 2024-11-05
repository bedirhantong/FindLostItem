package com.ribuufing.findlostitem.presentation.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.data.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    itemId: String,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val messages by chatViewModel.messages.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val lostItem by chatViewModel.lostItem.collectAsState()

    LaunchedEffect(Unit) {
        chatViewModel.addDummyData()
        chatViewModel.getLostItemById(itemId)
    }

    Scaffold(
        topBar = { ChatAppBar(navController, scrollBehavior,lostItem) },
        content = { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
//                    .imePadding() // Klavye açıldığında yukarı iter
            ) {
                MessageInputBar { content ->
                    coroutineScope.launch {
                        chatViewModel.addMessage(
                            Message(
                                id = messages.size + 1,
                                senderUser = User(
                                    "2",
                                    "Bedirhan Tong",
                                    imageUrl = "https://avatars.githubusercontent.com/u/70720131?v=4"
                                ),
                                receiverUser = User(
                                    "1", "İbrahim Serhan Baymaz",
                                    imageUrl = "https://avatars.githubusercontent.com/u/102352030?v=4"
                                ),
                                content = content,
                                date = "Just now"
                            )
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(navController: NavHostController, scrollBehavior: TopAppBarScrollBehavior, item : LostItem) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = item.foundWhere.toString(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back"
                )
            }
        },
        scrollBehavior = scrollBehavior

    )
}

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.senderUser.uid == "1") Arrangement.Start else Arrangement.End
    ) {
        if (message.senderUser.uid == "1") {
            UserImage(message.senderUser.imageUrl)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(
            horizontalAlignment = if (message.senderUser.uid == "1") Alignment.Start else Alignment.End
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
                        color = if (message.senderUser.uid == "1") Color(0xFFF0F5F5) else Color(
                            0xFFED822B
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = message.content, fontSize = 16.sp,
                    color = if (message.senderUser.uid == "1") Color.Black else Color.White,
                )
            }
            Text(
                text = message.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        if (message.senderUser.uid != "1") {
            Spacer(modifier = Modifier.width(8.dp))
            UserImage(message.senderUser.imageUrl)
        }
    }
}

@Composable
fun UserImage(imageUrl: String) {
    val painter = rememberAsyncImagePainter(model = imageUrl)
    val errorImage =
        rememberAsyncImagePainter(model = "https://avatars.githubusercontent.com/u/185503020?s=200&v=4")

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape) // Resmi dairesel yapmak için
            .border(1.dp, Color.Gray, CircleShape)
    ) {
        if (painter.state is AsyncImagePainter.State.Error) {
            Image(
                painter = errorImage,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}


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


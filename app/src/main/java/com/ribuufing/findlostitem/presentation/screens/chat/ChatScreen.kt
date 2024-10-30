package com.ribuufing.findlostitem.presentation.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.presentation.screens.chat.components.ChatAppBar
import com.ribuufing.findlostitem.presentation.screens.chat.components.MessageBubble
import com.ribuufing.findlostitem.presentation.screens.chat.components.MessageInputBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val messages by chatViewModel.messages.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        chatViewModel.addDummyData()
    }

    Scaffold(
        topBar = { ChatAppBar(navController, scrollBehavior) },
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
                                    2,
                                    "Bedirhan Tong",
                                    imageUrl = "https://avatars.githubusercontent.com/u/70720131?v=4"
                                ),
                                receiverUser = User(
                                    1, "İbrahim Serhan Baymaz",
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
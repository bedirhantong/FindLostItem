package com.ribuufing.findlostitem.presentation.directmessages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.presentation.directmessages.components.ChatListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DmChatScreen(
    navController: NavHostController,
    viewModel: DmChatViewModel = hiltViewModel()
) {
    val chatWithUsers by viewModel.chatWithUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Log.d("DmChatScreen", "Recomposing with isLoading: $isLoading, chats count: ${chatWithUsers.size}")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Messages") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFED822B),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Log.d("DmChatScreen", "Showing loading indicator")
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (chatWithUsers.isEmpty()) {
                Log.d("DmChatScreen", "No chats available")
                Text(
                    text = "No messages yet",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Log.d("DmChatScreen", "Showing chat list with ${chatWithUsers.size} items")
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chatWithUsers) { chatWithUser ->
                        Log.d("DmChatScreen", "Rendering chat item for user: ${chatWithUser.otherUser.name}")
                        ChatListItem(
                            chatWithUser = chatWithUser,
                            onClick = {
                                Log.d("DmChatScreen", "Chat item clicked: ${chatWithUser.chat.id}")
                                navController.navigate(
                                    "${Routes.Chat.route}/${chatWithUser.itemId}/${viewModel.currentUserId}/${chatWithUser.otherUser.uid}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

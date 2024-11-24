package com.ribuufing.findlostitem.presentation.messagesfeed

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.utils.Result
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesFeed(
    navController: NavHostController,
    viewModel: MessagesFeedViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val allChats by viewModel.allChats.collectAsState()
    val userInfos by viewModel.userInfos.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Messages") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (allChats.isEmpty()) {
                Text(
                    "No chats available",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn {
                    items(allChats) { chat ->
                        val otherUserUid = viewModel.getOtherUserUid(chat)
                        val otherUserInfo = userInfos[otherUserUid]
                        ChatRow(chat, otherUserInfo, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatRow(
    chat: Chat,
    userInfo: Result<User?>?,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
//                    Log.d("MessagesFeed", "receiver: ${chat.user2Id},   sender : ${chat.user1Id}")
//                    navController.navigate("chat/${chat.user2Id}/${chat.user1Id}")
                }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (userInfo) {
                is Result.Success -> {
                    val user = userInfo.data
                    if (user != null) {
                        ChatRowContent(user, chat)
                    } else {
                        Text(
                            text = "User not found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is Result.Failure -> {
                    userInfo.exception.localizedMessage?.let { Log.d("MessageFeed : ", it) }
                    Text(
                        text = "Error loading chat: ${userInfo.exception.localizedMessage}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        Divider(
            modifier = Modifier.padding(start = 80.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun ChatRowContent(user: User, chat: Chat) {
    // Avatar with online indicator
    Box {
        AsyncImage(
            model = user.imageUrl,
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(Color(0xFF4CAF50), CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .align(Alignment.BottomEnd)
        )
    }

    Spacer(modifier = Modifier.width(12.dp))

    // Name, message and badges
    Column(


        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatChatTime(1732398631382L),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Message status icon (if needed)
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Read",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "last message",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Unread count
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = 2.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun formatChatTime(timestamp: Long): String {
    if (timestamp == 0L) return ""

    val date = Date(timestamp)
    val now = Calendar.getInstance()
    val messageTime = Calendar.getInstance().apply { time = date }

    return when {
        // Today
        now.get(Calendar.DATE) == messageTime.get(Calendar.DATE) -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        }
        // Yesterday
        now.get(Calendar.DATE) - messageTime.get(Calendar.DATE) == 1 -> {
            "Yesterday"
        }
        // This week
        now.get(Calendar.WEEK_OF_YEAR) == messageTime.get(Calendar.WEEK_OF_YEAR) -> {
            SimpleDateFormat("EEE", Locale.getDefault()).format(date)
        }
        // Different year
        now.get(Calendar.YEAR) != messageTime.get(Calendar.YEAR) -> {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date)
        }
        // This year
        else -> {
            SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
        }
    }
}
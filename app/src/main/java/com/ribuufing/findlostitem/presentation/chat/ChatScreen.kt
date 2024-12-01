package com.ribuufing.findlostitem.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.utils.Result
import androidx.navigation.NavHostController
import com.ribuufing.findlostitem.data.model.LostItem
import coil.compose.AsyncImage
import com.ribuufing.findlostitem.presentation.chat.components.ChatBubble
import com.ribuufing.findlostitem.presentation.chat.components.MessageInputField
import com.ribuufing.findlostitem.presentation.home.formatTimestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    itemId: String,
    senderUid: String,
    receiverUid: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    
    val showScrollButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 2 || lazyListState.firstVisibleItemScrollOffset > 200
        }
    }

    fun smoothScrollToBottom() {
        scope.launch {
            try {
                val targetIndex = lazyListState.firstVisibleItemIndex / 2
                lazyListState.scrollToItem(targetIndex)
                
                delay(100)
                
                lazyListState.animateScrollToItem(
                    index = 0,
                    scrollOffset = 0
                )
            } catch (e: Exception) {
                lazyListState.scrollToItem(0)
            }
        }
    }

    LaunchedEffect(itemId, senderUid, receiverUid) {
        viewModel.createOrGetChat(itemId, senderUid, receiverUid)
    }

    val chatState by viewModel.chatState.collectAsState()
    val messagesState by viewModel.messagesState.collectAsState()
    val lostItemState by viewModel.lostItemState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var isItemDetailsVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                                                text = item.itemName,
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
                                                text = item.itemName,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = item.message,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = formatTimestamp(item.timestamp),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onPrimary,
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
                ) {
                    when (chatState) {
                        is Result.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color(0xFFED822B)
                            )
                        }
                        is Result.Success -> {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    when (messagesState) {
                                        is Result.Success -> {
                                            val messages = (messagesState as Result.Success<List<Message>>).data
                                            LazyColumn(
                                                state = lazyListState,
                                                modifier = Modifier.fillMaxSize(),
                                                reverseLayout = true,
                                                contentPadding = PaddingValues(vertical = 8.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                items(
                                                    items = messages,
                                                    key = { it.id }
                                                ) { message ->
                                                    val isCurrentUser = message.senderId == senderUid
                                                    ChatBubble(
                                                        message = message,
                                                        isCurrentUser = isCurrentUser,
                                                        userImage = viewModel.getUserImage(message.senderId),
                                                        userName = viewModel.getUserName(message.senderId) ?: "Unknown User"
                                                    )
                                                }
                                            }

                                            if (showScrollButton) {
                                                FloatingActionButton(
                                                    onClick = { smoothScrollToBottom() },
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .padding(bottom = 80.dp, end = 16.dp)
                                                        .size(46.dp),
                                                    containerColor = Color(0xFFED822B),
                                                    contentColor = Color.White
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.KeyboardArrowDown,
                                                        contentDescription = "Scroll to bottom",
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }
                                        is Result.Loading -> {
                                            CircularProgressIndicator(
                                                modifier = Modifier.align(Alignment.Center),
                                                color = Color(0xFFED822B)
                                            )
                                        }
                                        is Result.Failure -> {
                                            Text(
                                                text = "Couldn't load messages",
                                                modifier = Modifier.align(Alignment.Center),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                MessageInputField(
                                    messageText = messageText,
                                    onMessageChange = { messageText = it },
                                    onSendClick = {
                                        if (messageText.isNotBlank()) {
                                            viewModel.sendMessage(senderUid, messageText)
                                            messageText = ""
                                            scope.launch {
                                                lazyListState.animateScrollToItem(0)
                                            }
                                        }
                                    }
                                )
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
    }
}

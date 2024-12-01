package com.ribuufing.findlostitem.presentation.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import com.ribuufing.findlostitem.data.model.LostItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.presentation.nointernet.NoInternetScreen
import com.ribuufing.findlostitem.presentation.nointernet.NoInternetViewModel
import com.ribuufing.findlostitem.utils.Result
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemsScreen(
    noInternetViewModel: NoInternetViewModel = hiltViewModel(),
    viewModel: LostItemsViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val lostItems by viewModel.lostItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val isInternetAvailable by noInternetViewModel.isInternetAvailable
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchLostItems()
        noInternetViewModel.checkInternetConnection()
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.fetchLostItems()
            isRefreshing = false
        }
    }

    if (!isInternetAvailable) {
        openDialog.value = true
    } else {
        openDialog.value = false
    }

    NoInternetScreen(openDialog = openDialog, onRetry = {
        noInternetViewModel.checkInternetConnection()
    })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Lost Items",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("dmChatScreen") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.dm),
                            contentDescription = "Direct Message",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFFED822B)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { isRefreshing = true },
                indicator = { state, trigger ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = trigger,
                        contentColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.background
                    )
                }
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    if (isLoading) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(5) {
                                ShimmerEffect(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentSize()
                                )
                            }
                        }
                    } else {
                        if (lostItems.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No items found",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(lostItems) { item ->
                                    LostItemRow(item, viewModel, navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LostItemRow(item: LostItem, viewModel: LostItemsViewModel, navController: NavHostController) {
    var upvoteCount by remember { mutableIntStateOf(item.numOfUpVotes) }
    var downvoteCount by remember { mutableIntStateOf(item.numOfDownVotes) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    
    // Sender bilgilerini al
    val senderInfo by viewModel.getSenderInfo(item.senderInfo.senderId).collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                onClick = {
                    navController.navigate("item_detail/${item.itemId}")
                }
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.itemName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = formatTimestamp(item.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Text(
            text = item.message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.from_icon),
                    contentDescription = "Found Location icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF58B437)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.foundWhere,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF99704D)
                )
            }

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More icon",
                modifier = Modifier.size(16.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.placed_icon),
                    contentDescription = "Placed Location icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFD72224)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.placedWhere,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF99704D)
                )
            }
        }

        if (item.images.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val imageCount = item.images.size
                    val displayCount = minOf(imageCount, 4)
                    val rows = (displayCount + 1) / 2
                    
                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (displayCount == 1) 350.dp else 175.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val startIndex = row * 2
                            val endIndex = minOf(startIndex + 2, displayCount)
                            
                            for (i in startIndex until endIndex) {
                                val weight = if (displayCount == 1) 1f else 0.5f
                                Box(
                                    modifier = Modifier
                                        .weight(weight)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedImageUrl = item.images[i] }
                                ) {
                                    val painter = rememberAsyncImagePainter(model = item.images[i])
                                    Image(
                                        painter = painter,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    
                                    if (i == 3 && imageCount > 4) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.6f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "+${imageCount - 4}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (senderInfo) {
                            is Result.Success -> "Found by ${(senderInfo as Result.Success<User?>).data?.name ?: "Anonymous"}"
                            is Result.Failure -> "Found by Anonymous"
                            else -> "Found by Anonymous"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            viewModel.upvoteItem(item.itemId, upvoteCount)
                            upvoteCount++
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.thumb_up_like_svgrepo_com),
                            contentDescription = "Upvote",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = upvoteCount.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            viewModel.downVoteItem(item.itemId, downvoteCount)
                            downvoteCount--
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.thumb_down_svgrepo_com),
                            contentDescription = "Downvote",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = downvoteCount.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.share_02_svgrepo_com),
                    contentDescription = "Share",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        selectedImageUrl?.let { imageUrl ->
            ImagePreviewDialog(
                imageUrl = imageUrl,
                onDismiss = { selectedImageUrl = null },
                item = item
            )
        }
    }
}



fun formatTimestamp(timestamp: Timestamp): String {
    val date = timestamp.toDate()

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Gün/Ay/Yıl
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())     // Saat:Dakika

    val formattedDate = dateFormat.format(date)
    val formattedTime = timeFormat.format(date)

    return "$formattedDate $formattedTime"
}

@Composable
fun ShimmerEffect(
    modifier: Modifier,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
) {

    val shimmerColors = listOf(
        Color.Gray.copy(alpha = 0.3f),
        Color.Gray.copy(alpha = 0.5f),
        Color.Gray.copy(alpha = 1.0f),
        Color.Gray.copy(alpha = 0.5f),
        Color.Gray.copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
    )

    // Simulate LostItemRow structure
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(BorderStroke(0.1.dp, Color.Gray), shape = MaterialTheme.shapes.small)
            .padding(16.dp)
    ) {
        // Placeholder for the Item Name
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(20.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Placeholder for Location and Date Row
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Placeholder for Description
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(brush)
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImagePreviewDialog(
    imageUrl: String,
    onDismiss: () -> Unit,
    item: LostItem
) {
    val pagerState = rememberPagerState(
        initialPage = item.images.indexOf(imageUrl)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
        ) {
            HorizontalPager(
                count = item.images.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onDismiss)
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = item.images[page]
                    )
                    
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )

                    if (painter.state is AsyncImagePainter.State.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp),
                            color = Color.White
                        )
                    }
                }
            }

            // Sayfa göstergesi
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${item.images.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Kapatma butonu
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close preview",
                    tint = Color.White
                )
            }
        }
    }
}

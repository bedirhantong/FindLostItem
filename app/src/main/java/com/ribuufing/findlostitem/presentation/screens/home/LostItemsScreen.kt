package com.ribuufing.findlostitem.presentation.screens.home

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.presentation.screens.nointernet.NoInternetScreen
import com.ribuufing.findlostitem.presentation.screens.nointernet.NoInternetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemsScreen(
    noInternetViewModel: NoInternetViewModel = hiltViewModel(),
    viewModel: LostItemsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val lostItems by viewModel.filteredLostItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val isInternetAvailable by noInternetViewModel.isInternetAvailable
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refreshLostItems()
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        noInternetViewModel.checkInternetConnection()
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
                    IconButton(onClick = {

                    }) {
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
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                OutlinedTextField(
                    value = viewModel.searchQuery.collectAsState().value,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    label = {
                        Text(
                            "Search for items",
                            style = TextStyle(
                                color = Color(0xFF99704D),
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }, // Etiket metnini güncelle
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 7.dp, end = 7.dp, top = 7.dp, bottom = 12.dp),
                    shape = RoundedCornerShape(10.dp),
                    prefix = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = Color(0xFF99704D)
                        )
                    },
                    maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = Color(0xFFED822B),
                        focusedBorderColor = Color(0xFFED822B),
                        focusedPrefixColor = Color(0xFFED822B),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFFED822B),
                        focusedTextColor = Color(0xFF99704D),
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                // SwipeRefresh
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = { isRefreshing = true },
                    indicator = { state, trigger ->
                        SwipeRefreshIndicator(
                            state = state,
                            refreshTriggerDistance = trigger,
                            contentColor = MaterialTheme.colorScheme.primary,
                            backgroundColor = MaterialTheme.colorScheme.background,
                            elevation = 8.dp,
                        )
                    }
                ) {
                    val offsetY =
                        min(swipeRefreshState.indicatorOffset.dp, 80.dp) // Cap the movement

                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset(y = offsetY)
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
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .offset(y = offsetY),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Gönderiler bulunamadı",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .offset(y = offsetY)
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
        }
    )
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
fun LostItemRow(item: LostItem, viewModel: LostItemsViewModel, navController: NavHostController) {
    var upvoteCount by remember { mutableIntStateOf(item.numOfUpVotes) }
    var downvoteCount by remember { mutableIntStateOf(item.numOfDownVotes) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("item_detail/${item.id}")
            }
            .padding(10.dp)
            .border(
                border = BorderStroke(0.1.dp, Color.Gray),
                shape = MaterialTheme.shapes.small
            )
            .padding(16.dp)
    ) {
        // Item Name
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        // Location and Date Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.from_icon),
                        contentDescription = "Placed Location icon",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF58B437)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.foundWhere.toString(),
                        style = MaterialTheme.typography.bodySmall,
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
                        text = item.placedWhere.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF99704D)
                    )
                }
            }

            Text(
                text = item.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF99704D)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Images Horizontal Pager
        if (item.images.isNotEmpty()) {
            val pagerState = rememberPagerState()
            Column {
                HorizontalPager(
                    count = item.images.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) { page ->
                    val painter = rememberAsyncImagePainter(model = item.images[page])
                    val painterState = painter.state

                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        // Placeholder or Loading indicator
                        if (painterState is AsyncImagePainter.State.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(32.dp)
                            )
                        }

                        // Error indicator
                        if (painterState is AsyncImagePainter.State.Error) {
                            Text(
                                text = "Failed to load image.",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    repeat(item.images.size) { index ->
                        val color =
                            if (index == pagerState.currentPage) Color.Black else Color.LightGray
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                                .padding(2.dp)
                        )
                        if (index < item.images.size - 1) {
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.upvoteItem(item.id.toString(), upvoteCount)
                    upvoteCount++
                },
                modifier = Modifier.wrapContentWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Upvote button"
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = upvoteCount.toString(), modifier = Modifier.wrapContentWidth())

            IconButton(
                onClick = {
                    viewModel.downVoteItem(item.id.toString(), downvoteCount)
                    downvoteCount--
                },
                modifier = Modifier.wrapContentWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Downvote button"
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = downvoteCount.toString(), modifier = Modifier.wrapContentWidth())
            IconButton(
                onClick = {
                    // Share the item
                },
                modifier = Modifier.wrapContentWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share button"
                )
            }
        }
    }
}
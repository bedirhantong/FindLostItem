package com.ribuufing.findlostitem.presentation.screens.home

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ribuufing.findlostitem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemsScreen(viewModel: LostItemsViewModel = hiltViewModel()) {
    val lostItems by viewModel.lostItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 3 saniyelik shimmer gösterimi
    LaunchedEffect(Unit) {
        // internet kontrolü yapılabilir
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lost Items",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { /* DM button action here */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.dm), // Replace with your drawable name
                            contentDescription = "Direct Message",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        content = {
            if (isLoading) {
                // İstek devam ederken shimmer effect göster
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(5) {
                        ShimmerEffect(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }
                }
            } else {
                if (lostItems.isEmpty()) {
                    // Veri boşsa ekrana mesaj göster
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Gönderiler bulunamadı",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                } else {
                    // Veriler varsa listeyi göster
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        items(lostItems) { item ->
                            LostItemRow(item, viewModel)
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
                .height(100.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Placeholder for Description
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
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
fun LostItemRow(item: LostItem, viewModel: LostItemsViewModel) {
    var upvoteCount by remember { mutableIntStateOf(item.numOfUpVotes) }
    var downvoteCount by remember { mutableIntStateOf(item.numOfDownVotes) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            // Set the border for the post
            .border(
                border = BorderStroke(0.1.dp, Color.Gray),
                shape = MaterialTheme.shapes.small
            )
            .padding(16.dp) // Padding inside the border
    ) {
        // Item Name
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Location and Date Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Found Where
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Found Location icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.foundWhere, // Found location text
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Three Dot Icon
                Icon(
                    imageVector = Icons.Default.MoreVert, // Three dot vertical icon
                    contentDescription = "More icon",
                    modifier = Modifier.size(16.dp)
                )

                // Placed Where
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Placed Location icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.placedWhere, // Placed location text
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Date
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyMedium,
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
                        .height(250.dp) // Set a fixed height for the image display
                ) { page ->
                    val painter: Painter = rememberAsyncImagePainter(model = item.images[page])
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit // Adjust content scale as needed
                    )
                }

                // Indicator at the bottom
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                ) {
                    repeat(item.images.size) { index ->
                        val color = if (index == pagerState.currentPage) Color.Black else Color.Gray
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                                .padding(2.dp)
                        )
                        if (index < item.images.size - 1) {
                            Spacer(modifier = Modifier.width(4.dp)) // Space between indicators
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
                    // Downvote action
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
                    // Share the item with whatsapp, email, etc.

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
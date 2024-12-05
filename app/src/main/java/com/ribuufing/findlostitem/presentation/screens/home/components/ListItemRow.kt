package com.ribuufing.findlostitem.presentation.screens.home.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.presentation.screens.home.LostItemsViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LostItemRow(item: LostItem, viewModel: LostItemsViewModel) {
    var upvoteCount by remember { mutableIntStateOf(item.numOfUpVotes) }
    var downvoteCount by remember { mutableIntStateOf(item.numOfDownVotes) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                        text = item.foundWhere,
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
                        text = item.placedWhere,
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
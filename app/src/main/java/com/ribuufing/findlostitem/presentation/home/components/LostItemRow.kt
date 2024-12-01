package com.ribuufing.findlostitem.presentation.home.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.presentation.home.LostItemsViewModel
import com.ribuufing.findlostitem.presentation.home.formatTimestamp
import com.ribuufing.findlostitem.presentation.home.toShareText
import com.ribuufing.findlostitem.utils.Result

@Composable
fun LostItemRow(item: LostItem, viewModel: LostItemsViewModel, navController: NavHostController) {
    var upvoteCount by remember { mutableIntStateOf(item.numOfUpVotes) }
    var downvoteCount by remember { mutableIntStateOf(item.numOfDownVotes) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    // Sender bilgilerini al
    val senderInfo by viewModel.getSenderInfo(item.senderInfo.senderId).collectAsState()

    val context = LocalContext.current

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

            IconButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, item.toShareText())
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Lost Item"))
                }
            ) {
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

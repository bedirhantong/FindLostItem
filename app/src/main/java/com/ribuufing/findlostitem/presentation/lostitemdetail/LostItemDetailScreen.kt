package com.ribuufing.findlostitem.presentation.lostitemdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.presentation.home.formatTimestamp

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LostItemDetailScreen(
    navController: NavHostController,
    itemId: String,
    viewModel: LostItemDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(itemId) {
        viewModel.getLostItemById(itemId)
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val lostItem by viewModel.lostItem.collectAsState()
    val currentUserUid by viewModel.currentUserUid.collectAsState()

    val pagerState = rememberPagerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

        },
        content = { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                lostItem?.let { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = paddingValues.calculateTopPadding(),
                                bottom = paddingValues.calculateBottomPadding()
                            )
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = item.itemName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Place details
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Place, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = item.foundWhere, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = item.placedWhere, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Text(
                            text = formatTimestamp(item.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF99704D)
                        )

                        // Image slider
                        if (item.images.isNotEmpty()) {
                            HorizontalPager(
                                count = item.images.size,
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            ) { page ->
                                val painter: Painter = rememberAsyncImagePainter(model = item.images[page])
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        // Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    val receiverUid = item.senderInfo.senderId ?: return@Button
                                    currentUserUid?.let { senderUid ->
                                        navController.navigate("${Routes.Chat.route}/${item.itemId}/$senderUid/$receiverUid")
                                    }
                                }
                            ) {
                                Text("Message", color = Color.White)
                            }
                            OutlinedButton(
                                onClick = { /* Dial item contact number */ },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFC8C03))
                            ) {
                                Text("Call")
                            }
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Item details not found")
                    }
                }
            }
        }
    )
}
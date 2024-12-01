package com.ribuufing.findlostitem.presentation.home

import com.ribuufing.findlostitem.data.model.LostItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.presentation.nointernet.NoInternetScreen
import com.ribuufing.findlostitem.presentation.nointernet.NoInternetViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import com.ribuufing.findlostitem.presentation.home.components.LostItemRow
import com.ribuufing.findlostitem.presentation.home.components.ShimmerEffect

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


fun formatTimestamp(timestamp: Timestamp): String {
    val date = timestamp.toDate()

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Gün/Ay/Yıl
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())     // Saat:Dakika

    val formattedDate = dateFormat.format(date)
    val formattedTime = timeFormat.format(date)

    return "$formattedDate $formattedTime"
}


fun LostItem.toShareText(): String {
    return buildString {
        appendLine("🔍 Lost Item Details")
        appendLine("📦 Item: $itemName")
        appendLine("📝 Description: $message")
        appendLine()
        appendLine("📍 Found at: $foundWhere")
        appendLine("📍 Placed at: $placedWhere")
        appendLine()
        appendLine("⏰ Date: ${formatTimestamp(timestamp)}")
        appendLine()
        appendLine("If you have any information about this item, please contact through the app.")
        appendLine("Download Find Lost Item app to help others find their lost items!")
    }
}

package com.ribuufing.findlostitem.presentation.screens.home

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
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.presentation.screens.home.components.LostItemRow
import com.ribuufing.findlostitem.presentation.screens.home.components.ShimmerEffect
import com.ribuufing.findlostitem.presentation.screens.nointernet.NoInternetScreen
import com.ribuufing.findlostitem.presentation.screens.nointernet.NoInternetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemsScreen(
    noInternetViewModel: NoInternetViewModel = hiltViewModel(),
    viewModel: LostItemsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val lostItems by viewModel.lostItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val isInternetAvailable by noInternetViewModel.isInternetAvailable // İnternet durumunu gözlemle
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            // Trigger refresh logic
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
                modifier = Modifier.fillMaxWidth().imePadding(),
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
                        navController.navigate(Routes.Chat.route)
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
                val offsetY = min(swipeRefreshState.indicatorOffset.dp, 80.dp)

                Box(modifier = Modifier.fillMaxSize()) {
                    if (isLoading) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
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
                                    .offset(y = offsetY),  // Move content down as user swipes
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
                                    .padding(it)
                                    .offset(y = offsetY)
                            ) {
                                items(lostItems) { item ->
                                    LostItemRow(item, viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}


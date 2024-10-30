package com.ribuufing.findlostitem.presentation.screens.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.presentation.screens.nointernet.NoInternetScreen
import com.ribuufing.findlostitem.presentation.screens.nointernet.NoInternetViewModel
import androidx.compose.ui.draw.clip
import com.ribuufing.findlostitem.presentation.screens.home.components.ShimmerEffect
import com.ribuufing.findlostitem.presentation.screens.profile.presentation.components.ProfileTabPager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    noInternetViewModel: NoInternetViewModel = hiltViewModel(),
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val userInfos by viewModel.userInfos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val isInternetAvailable by noInternetViewModel.isInternetAvailable // İnternet durumunu gözlemle
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refreshUserInfos()
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        noInternetViewModel.checkInternetConnection()
    }

    openDialog.value = !isInternetAvailable

    NoInternetScreen(openDialog = openDialog, onRetry = {
        noInternetViewModel.checkInternetConnection()
    })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                title = {
                    Text(
                        text = "PROFILE",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Routes.Settings.route)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = "Settings Button",
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
                val offsetY = min(swipeRefreshState.indicatorOffset.dp, 80.dp) // Cap the movement

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
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                                .offset(y = offsetY)
                        ) {

                            val painter =
                                rememberAsyncImagePainter(model = "https://www.pngarts.com/files/2/Cristiano-Ronaldo-PNG-High-Quality-Image.png")
                            val painterState = painter.state

                            Box() {
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Fit
                                )

                                if (painterState is AsyncImagePainter.State.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(32.dp)
                                    )
                                }

                                if (painterState is AsyncImagePainter.State.Error) {
                                    Text(
                                        text = "Failed to load image.",
                                        color = Color.Red,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            Text(
                                text = "User Name",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Create Date",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ProfileTabPager(it)

                        }
                    }
                }

            }
        }
    )
}
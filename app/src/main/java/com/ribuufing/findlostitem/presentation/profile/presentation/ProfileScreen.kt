package com.ribuufing.findlostitem.presentation.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.presentation.home.ShimmerEffect
import com.ribuufing.findlostitem.presentation.nointernet.NoInternetScreen
import com.ribuufing.findlostitem.presentation.nointernet.NoInternetViewModel
import com.google.accompanist.pager.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.ui.draw.clip
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.ribuufing.findlostitem.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    noInternetViewModel: NoInternetViewModel = hiltViewModel(),
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val isLoading by viewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val isInternetAvailable by noInternetViewModel.isInternetAvailable
    val openDialog = remember { mutableStateOf(false) }
    val userInfoState = viewModel.userInfos.collectAsState().value

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            // Trigger refresh logic
            viewModel.refreshUserInfos()
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshUserInfos()
        noInternetViewModel.checkInternetConnection()
    }

    openDialog.value = !isInternetAvailable

    NoInternetScreen(openDialog = openDialog, onRetry = {
        noInternetViewModel.checkInternetConnection() // Retry checking the connection
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
                // Content of the screen
                val offsetY = min(swipeRefreshState.indicatorOffset.dp, 80.dp) // Cap the movement

                Box(modifier = Modifier.fillMaxSize()) {
                    if (isLoading) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                                .offset(y = offsetY)  // Move content down as user swipes
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
                                .offset(y = offsetY)  // Move content down as user swipes
                        ) {
                            // Check user data and display accordingly
                            when (userInfoState) {
                                is Result.Success -> {
                                    val user = userInfoState.data
                                    if (user != null) {
                                        ProfileContent(user)
                                    } else {
                                        Text("No user data available")
                                    }
                                }
                                is Result.Failure -> {
                                    Text("Error: ${userInfoState.exception.localizedMessage}")
                                }
                                else -> {
                                    Text("Unknown state")
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
fun ProfileContent(user: User) {
    // Display the user information on the screen
    Column(modifier = Modifier.padding(16.dp)) {
        // Display the user's profile image
        val painter = rememberAsyncImagePainter(model = user.imageUrl.ifEmpty { "https://cdn.pixabay.com/photo/2014/03/25/16/24/female-296989_1280.png" })
        val painterState = painter.state

        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Image(
                painter = painter,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Fit
            )

            // Loading and error states for image
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

        // User details
        Text(
            text = "Name: ${user.name}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Email: ${user.email}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Phone: ${user.phone.ifEmpty { "Not provided" }}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "UID: ${user.uid}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Optionally, display the user's created items or chats if available
        if (user.foundedItems.isNotEmpty()) {
            Text("Founded Items: ${user.foundedItems.size}")
        }

        if (user.chats.isNotEmpty()) {
            Text("Chats: ${user.chats.size}")
        }

        Spacer(modifier = Modifier.height(16.dp))

//        TabPagerExample(it = PaddingValues(0.dp), foundItems = user.foundedItems)


    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabPagerExample(it: PaddingValues, foundItems: List<LostItem> = emptyList()) {
    val tabs = listOf(
        "Found items"
//        , "Tab 2", "Tab 3"
    ) // Sekme başlıkları
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
    ) {
        // Tab başlıkları
        TabRow(
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .pagerTabIndicatorOffset(pagerState, tabPositions)
                        .height(0.2.dp),
                    color = Color.Black
                )
            },
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            tabs = {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        text = {
                            Text(title, fontWeight = FontWeight.Bold)
                        }
                    )
                }
            }
        )

        // Sayfalar arası geçiş için HorizontalPager
        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) { page ->
            when (page) {
                0 -> ListContent(foundItems)
                1 -> ListContent(foundItems)
                2 -> ListContent(foundItems)
            }
        }
    }
}

@Composable
fun ListContent( foundItems: List<LostItem> = emptyList()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
    ) {
        items(foundItems.size) { index ->
            LostItemRow(
                item = foundItems[index]
            )
        }
    }
}

@Composable
fun LostItemRow(item: LostItem) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image and text side-by-side
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Image with loading/error states
            val painter = rememberAsyncImagePainter(model = item.images[0])
            val painterState = painter.state

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .padding(end = 16.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Loading indicator
                if (painterState is AsyncImagePainter.State.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp)
                    )
                }

                // Error indicator
                if (painterState is AsyncImagePainter.State.Error) {
                    Text(
                        text = "Failed to load",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Item information column
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                // Title
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                )

                // Date
                Text(
                    text = "Lost on ${item.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Trash icon
        IconButton(
            onClick = {
                // Trash button action
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.trash),
                contentDescription = "Delete item",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF000000)
            )
        }
    }
}

package com.ribuufing.findlostitem.presentation.profile.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.presentation.nointernet.NoInternetScreen
import com.ribuufing.findlostitem.presentation.nointernet.NoInternetViewModel
import com.ribuufing.findlostitem.presentation.profile.components.ProfileContent
import com.ribuufing.findlostitem.utils.Result

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
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
    val lostItems by viewModel.lostItems.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshUserInfos()
        noInternetViewModel.checkInternetConnection()
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refreshUserInfos()
            isRefreshing = false
        }
    }

    openDialog.value = !isInternetAvailable

    NoInternetScreen(openDialog = openDialog, onRetry = {
        noInternetViewModel.checkInternetConnection()
    })

    Scaffold(
        backgroundColor = Color.Transparent,
        content = {
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(50.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        userInfoState is Result.Success -> {
                            val user = userInfoState.data
                            if (user != null) {
                                ProfileContent(
                                    user = user,
                                    foundItems = lostItems,
                                    onSettingsClick = {
                                        navController.navigate(Routes.Settings.route)
                                    },
                                    onRefresh = { isRefreshing = true },
                                    isRefreshing = isRefreshing
                                )
                            } else {
                                Text(
                                    "No user data available",
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        userInfoState is Result.Failure -> {
                            Text(
                                text = "Error: ${userInfoState.exception.localizedMessage}",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                            )
                        }
                        else -> {
                            Text(
                                text = "Loading user data...",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    )
}

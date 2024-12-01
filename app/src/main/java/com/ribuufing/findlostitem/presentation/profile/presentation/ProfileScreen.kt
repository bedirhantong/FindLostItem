package com.ribuufing.findlostitem.presentation.profile.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
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
    val userInfoState by viewModel.userInfos.collectAsState()
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
    ) { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { isRefreshing = true }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (userInfoState) {
                    is Result.Success -> {
                        val user = (userInfoState as Result.Success).data
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
                        }
                    }
                    is Result.Failure -> {
                        Text(text = "Error: ${(userInfoState as Result.Failure).exception.localizedMessage}")
                    }
                    is Result.Loading -> {
                        isRefreshing = true
                    }
                    else -> {
                        LaunchedEffect(Unit) {
                            viewModel.refreshUserInfos()
                        }
                    }
                }
            }
        }
    }
}

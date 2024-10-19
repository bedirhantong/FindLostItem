package com.ribuufing.findlostitem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ribuufing.findlostitem.presentation.screens.home.LostItemsScreen
import com.ribuufing.findlostitem.presentation.screens.onboarding.WelcomeScreen

@Composable
fun NavigationGraph(navController: NavHostController, onBottomBarVisibility: (Boolean) -> Unit) {
    NavHost(navController, startDestination = Routes.Welcome.route) {
        composable(Routes.Welcome.route) {
            onBottomBarVisibility(false)
            WelcomeScreen(navController = navController)
        }
        composable(BottomNavigationItems.Home.route) {
            onBottomBarVisibility(true)
            LostItemsScreen()
        }
        composable(BottomNavigationItems.AddItem.route) {
            onBottomBarVisibility(true)
            LostItemsScreen()
        }
        composable(BottomNavigationItems.Profile.route) {
            onBottomBarVisibility(true)
            LostItemsScreen()
        }
    }

}
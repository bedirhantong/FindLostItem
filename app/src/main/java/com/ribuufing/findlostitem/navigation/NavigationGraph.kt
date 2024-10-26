package com.ribuufing.findlostitem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ribuufing.findlostitem.presentation.screens.chat.ChatScreen
import com.ribuufing.findlostitem.presentation.screens.home.LostItemsScreen
import com.ribuufing.findlostitem.presentation.screens.onboarding.WelcomeScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.ribuufing.findlostitem.presentation.screens.auth.presentation.login.LoginScreen
import com.ribuufing.findlostitem.presentation.screens.auth.presentation.signup.RegisterScreen
import com.ribuufing.findlostitem.presentation.screens.profile.presentation.SettingsScreen
import com.ribuufing.findlostitem.presentation.screens.reportfounditem.ReportFoundItemScreen

@Composable
fun NavigationGraph(navController: NavHostController, onBottomBarVisibility: (Boolean) -> Unit) {
    NavHost(navController, startDestination = Routes.Welcome.route) {
        composable(Routes.Welcome.route) {
            onBottomBarVisibility(false)
            WelcomeScreen(navController = navController)
        }
        composable(BottomNavigationItems.Home.route) {
            onBottomBarVisibility(true)
            LostItemsScreen(navController = navController)
        }
        composable(BottomNavigationItems.AddItem.route) {
            onBottomBarVisibility(true)
            ReportFoundItemScreen()
        }
        composable(BottomNavigationItems.MapItem.route) {
            onBottomBarVisibility(true)
            LostItemsScreen(navController = navController)
        }
        composable(BottomNavigationItems.Profile.route) {
            onBottomBarVisibility(true)
            SettingsScreen(navController = navController)
        }
        composable(Routes.Chat.route, enterTransition = ::slideInToRight,
            exitTransition = ::slideOutToRight) {
            onBottomBarVisibility(false)
            ChatScreen(navController)
        }
        composable(Routes.Signup.route, enterTransition = ::slideInToLeft, exitTransition = ::slideOutToLeft) {
            onBottomBarVisibility(false)
            RegisterScreen(navController = navController)
        }
        composable(Routes.Login.route, enterTransition = ::slideInToLeft, exitTransition = ::slideOutToLeft) {
            onBottomBarVisibility(false)
            LoginScreen(navController = navController)
        }
    }
}

fun slideInToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    )
}

fun slideInToRight(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    )
}

fun slideOutToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    )
}

fun slideOutToRight(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    )
}
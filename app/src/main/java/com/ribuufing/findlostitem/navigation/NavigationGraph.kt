package com.ribuufing.findlostitem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ribuufing.findlostitem.presentation.chat.ChatScreen
import com.ribuufing.findlostitem.presentation.home.LostItemsScreen
import com.ribuufing.findlostitem.presentation.onboarding.WelcomeScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ribuufing.findlostitem.presentation.auth.presentation.login.LoginScreen
import com.ribuufing.findlostitem.presentation.auth.presentation.signup.RegisterScreen
import com.ribuufing.findlostitem.presentation.directmessages.DmChatScreen
import com.ribuufing.findlostitem.presentation.lostitemdetail.LostItemDetailScreen
import com.ribuufing.findlostitem.presentation.mapscreen.MapScreen
import com.ribuufing.findlostitem.presentation.paywall.PaywallScreen
import com.ribuufing.findlostitem.presentation.profile.presentation.ProfileScreen
import com.ribuufing.findlostitem.presentation.profile.presentation.settings.SettingsScreen
import com.ribuufing.findlostitem.presentation.reportfounditem.ReportFoundItemScreen
import com.ribuufing.findlostitem.presentation.search.SearchScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String,
    onBottomBarVisibility: (Boolean) -> Unit
) {
    NavHost(navController, startDestination = startDestination) {
        composable(Routes.Welcome.route) {
            onBottomBarVisibility(false)
            WelcomeScreen(navController = navController)
        }
        composable(BottomNavigationItems.Home.route) {
            onBottomBarVisibility(true)
            LostItemsScreen(navController = navController)
        }
        composable(BottomNavigationItems.AddItem.route) {
            onBottomBarVisibility(false)
            ReportFoundItemScreen(
                navController = navController,
                onNavigateToHome = {
                    navController.navigate(BottomNavigationItems.Home.route) {
                        popUpTo(BottomNavigationItems.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(BottomNavigationItems.MapItem.route) {
            onBottomBarVisibility(false)
            MapScreen(navController = navController)
        }
        composable(BottomNavigationItems.Profile.route) {
            onBottomBarVisibility(true)
            ProfileScreen(navController = navController)
        }
        composable(Routes.Paywall.route) {
            PaywallScreen(navController = navController)
        }

        composable(
            route = "${Routes.Chat.route}/{itemId}/{senderUid}/{receiverUid}",
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType },
                navArgument("senderUid") { type = NavType.StringType },
                navArgument("receiverUid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Parametreleri al
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            val senderUid = backStackEntry.arguments?.getString("senderUid") ?: return@composable
            val receiverUid = backStackEntry.arguments?.getString("receiverUid") ?: return@composable
            onBottomBarVisibility(false)
            ChatScreen(navController = navController, itemId = itemId, senderUid = senderUid, receiverUid = receiverUid)
        }

        composable("dmChatScreen") {
            onBottomBarVisibility(false)
            DmChatScreen(navController = navController)
        }

        composable(
            Routes.Signup.route,
            enterTransition = ::slideInToLeft,
            exitTransition = ::slideOutToLeft
        ) {
            onBottomBarVisibility(false)
            RegisterScreen(navController = navController)
        }
        composable(
            Routes.Login.route,
            enterTransition = ::slideInToLeft,
            exitTransition = ::slideOutToLeft
        ) {
            onBottomBarVisibility(false)
            LoginScreen(navController = navController)
        }
        composable(
            Routes.Settings.route,
            enterTransition = ::slideInToLeft,
            exitTransition = ::slideOutToLeft
        ) {
            onBottomBarVisibility(false)
            SettingsScreen(navController = navController)
        }

        composable(
            route = Routes.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            onBottomBarVisibility(false)
            LostItemDetailScreen(navController, itemId)
        }

        composable("search") {
            onBottomBarVisibility(true)
            SearchScreen(navController = navController)
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
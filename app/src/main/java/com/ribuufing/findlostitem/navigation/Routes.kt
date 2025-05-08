package com.ribuufing.findlostitem.navigation

sealed class Routes(val route: String) {
    object Welcome : Routes("welcome")
    object Messages : Routes("messages")
    object Chat : Routes("chat/{itemId}/{receiverId}")
    object Signup : Routes("signup")
    object Login : Routes("login")
    object Settings : Routes("settings")
    object Paywall : Routes("paywall")
    object ItemDetail : Routes("item_detail/{itemId}")
}
package com.ribuufing.findlostitem.navigation

sealed class Routes(val route: String) {
    object Welcome : Routes("welcome")
    object Chat : Routes("chat")
    object Signup : Routes("signup")
    object Login : Routes("login")
    object Settings : Routes("settings")
}
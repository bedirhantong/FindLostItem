package com.ribuufing.findlostitem.navigation

sealed class Routes(val route: String) {
    object Welcome : Routes("welcome")
}
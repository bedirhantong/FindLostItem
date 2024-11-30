package com.ribuufing.findlostitem.navigation

import com.ribuufing.findlostitem.R

sealed class BottomNavigationItems(
    val route: String,
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int = selectedIcon
) {

    object Home : BottomNavigationItems(
        "home", "Home", R.drawable.home, R.drawable.home_light
    )

    object Search : BottomNavigationItems(
        "search", "Search", R.drawable.search, R.drawable.search
    )

    object AddItem : BottomNavigationItems(
        "add_item", "Post", R.drawable.add_light, R.drawable.add
    )

    object MapItem : BottomNavigationItems(
        "map_item", "Map", R.drawable.map_logo, R.drawable.map_logo
    )

    object Profile : BottomNavigationItems(
        "profile", "Profile", R.drawable.profile_light, R.drawable.profile_dark
    )
}
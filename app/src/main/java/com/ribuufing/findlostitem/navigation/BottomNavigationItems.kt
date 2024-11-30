package com.ribuufing.findlostitem.navigation

import com.ribuufing.findlostitem.R

sealed class BottomNavigationItems(
    val route: String,
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    object Home : BottomNavigationItems(
        route = "home",
        title = "Home",
        selectedIcon = R.drawable.home,
        unselectedIcon = R.drawable.home_light
    )

    object Search : BottomNavigationItems(
        route = "search",
        title = "Search",
        selectedIcon = R.drawable.search,
        unselectedIcon = R.drawable.search
    )

    object AddItem : BottomNavigationItems(
        route = "add_item",
        title = "Post",
        selectedIcon = R.drawable.add_light,
        unselectedIcon = R.drawable.add
    )

    object MapItem : BottomNavigationItems(
        route = "map_item",
        title = "Map",
        selectedIcon = R.drawable.map_logo,
        unselectedIcon = R.drawable.map_logo
    )

    object Profile : BottomNavigationItems(
        route = "profile",
        title = "Profile",
        selectedIcon = R.drawable.profile_light,
        unselectedIcon = R.drawable.profile_dark
    )
}
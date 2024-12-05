package com.ribuufing.findlostitem.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavHostController,
    state: Boolean,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        BottomNavigationItems.Home,
        BottomNavigationItems.Search,
        BottomNavigationItems.AddItem,
        BottomNavigationItems.MapItem,
        BottomNavigationItems.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        screens.forEachIndexed { index, screen ->
            if (screen == BottomNavigationItems.AddItem) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        containerColor = Color(0xFFED822B),
                        modifier = Modifier.size(66.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = screen.selectedIcon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (currentRoute == screen.route) 
                                    screen.selectedIcon 
                                else 
                                    screen.unselectedIcon
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFED822B),
                        selectedTextColor = Color(0xFFED822B),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}

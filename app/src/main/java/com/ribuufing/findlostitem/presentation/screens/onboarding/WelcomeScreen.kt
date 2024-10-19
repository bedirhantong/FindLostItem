package com.ribuufing.findlostitem.presentation.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.ribuufing.findlostitem.navigation.BottomNavigationItems

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Column (
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ){
        Button(
            onClick = {
                navController.navigate(BottomNavigationItems.Home.route)
            },
        ) {
            Text(text = "Welcome")
        }
    }
}
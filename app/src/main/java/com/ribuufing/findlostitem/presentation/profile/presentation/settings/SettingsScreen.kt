package com.ribuufing.findlostitem.presentation.profile.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ribuufing.findlostitem.navigation.BottomNavigationItems
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.utils.Result

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController
) {
    val logoutState by viewModel.logoutState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Log Out Button
        Button(
            onClick = {
                viewModel.logout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Log out")
        }

        // Observe logout state
        when (logoutState) {
            is Result.Success -> {
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.Login.route) {
                    // Back stack'ı temizlemek için ayarları yapılandır
                    popUpTo(BottomNavigationItems.Profile.route) {
                        inclusive = true
                    }
                }
            }

            is Result.Failure -> {
                Toast.makeText(context, "Failed to log out", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }
}

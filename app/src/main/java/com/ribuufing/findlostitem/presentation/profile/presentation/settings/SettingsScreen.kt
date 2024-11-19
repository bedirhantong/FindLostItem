package com.ribuufing.findlostitem.presentation.profile.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
// For Material3, use this import
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ribuufing.findlostitem.navigation.BottomNavigationItems
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.utils.Result

@OptIn(ExperimentalMaterial3Api::class) // Use Material3
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController
) {
    val logoutState by viewModel.logoutState.collectAsState()
    val context = LocalContext.current

    // State for showing/hiding the logout confirmation dialog
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(logoutState) {
        logoutState?.let {
            when (it) {
                is Result.Success -> {
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate(Routes.Login.route) {
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

    // Main screen scaffold with app bar
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFED822B),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Account settings section
            SettingsCategory(
                title = "Account Settings",
                items = listOf(
                    SettingsItem(
                        label = "Profile",
                        description = "View and edit profile",
                        onClick = { /* Navigate to profile settings */ },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
                    ),
                    SettingsItem(
                        label = "Log Out",
                        description = "Log out of the app",
                        onClick = { showLogoutDialog = true },
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Log Out") },
                        highlighted = true
                    )
                )
            )


            // Preferences and other sections can follow...

            // Show the logout confirmation dialog when needed
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Confirm Logout") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.logout()
                            showLogoutDialog = false
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsCategory(title: String, items: List<SettingsItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color(0xFFED822B)
        )
        items.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.highlighted) Color(0xFFED822B).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.elevatedCardElevation(),
                onClick = item.onClick
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item.icon()
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (item.highlighted) FontWeight.Bold else FontWeight.Normal
                        )
                        item.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

data class SettingsItem(
    val label: String,
    val description: String? = null,
    val onClick: () -> Unit, // This is a lambda function, not a Composable
    val icon: @Composable () -> Unit,
    val highlighted: Boolean = false
)


package com.ribuufing.findlostitem.presentation.profile.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController
) {
    val logoutState by viewModel.logoutState.collectAsState()
    val context = LocalContext.current
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
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
                .padding(horizontal = 16.dp)
        ) {
            SettingsCategory(
                title = "Account",
                items = listOf(
                    SettingsItem(
                        label = "Profile Settings",
                        description = "Update your profile information",
                        onClick = { },
                        icon = { 
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFFED822B)
                            )
                        }
                    ),
                    SettingsItem(
                        label = "Notifications",
                        description = "Manage your notifications",
                        onClick = { },
                        icon = { 
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color(0xFFED822B)
                            )
                        }
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            SettingsCategory(
                title = "App Settings",
                items = listOf(
                    SettingsItem(
                        label = "Log Out",
                        description = "Sign out of your account",
                        onClick = { showLogoutDialog = true },
                        icon = { 
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Color(0xFFED822B)
                            )
                        },
                        highlighted = true
                    )
                )
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                icon = { 
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFED822B)
                    )
                },
                title = { 
                    Text(
                        "Log Out",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = { 
                    Text(
                        "Are you sure you want to log out?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.logout()
                            showLogoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFED822B)
                        )
                    ) {
                        Text("Log Out")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showLogoutDialog = false },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFED822B)
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsCategory(title: String, items: List<SettingsItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFED822B)
            ),
            modifier = Modifier.padding(vertical = 12.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                items.forEachIndexed { index, item ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = item.onClick,
                        color = if (item.highlighted) 
                            Color(0xFFED822B).copy(alpha = 0.1f) 
                        else 
                            MaterialTheme.colorScheme.surface
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
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (item.highlighted) 
                                            FontWeight.Bold 
                                        else 
                                            FontWeight.Normal
                                    )
                                )
                                item.description?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    if (index < items.size - 1) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

data class SettingsItem(
    val label: String,
    val description: String? = null,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val highlighted: Boolean = false
)


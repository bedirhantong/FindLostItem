package com.ribuufing.findlostitem

import android.annotation.SuppressLint
import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.ribuufing.findlostitem.navigation.BottomBar
import com.ribuufing.findlostitem.navigation.BottomNavigationItems
import com.ribuufing.findlostitem.navigation.NavigationGraph
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.ui.theme.FindLostItemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        // Kullanıcı oturum durumunu kontrol et
        val startDestination = if (Firebase.auth.currentUser != null) {
            BottomNavigationItems.Home.route
        } else {
            Routes.Welcome.route
        }

        setContent {
            FindLostItemTheme {
                MainContent(startDestination)
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun MainContent(startDestination: String) {
        val navController: NavHostController = rememberNavController()
        var buttonsVisible by remember { mutableStateOf(false) }

        Scaffold(
            bottomBar = {
                if (buttonsVisible) {
                    BottomBar(navController = navController, state = buttonsVisible)
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (buttonsVisible) 76.dp else 0.dp)
            ) {
                NavigationGraph(
                    navController = navController,
                    startDestination = startDestination
                ) { isVisible ->
                    buttonsVisible = isVisible
                }
            }
        }
    }
}

package com.ribuufing.findlostitem

import android.annotation.SuppressLint
import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.ribuufing.findlostitem.navigation.BottomBar
import com.ribuufing.findlostitem.navigation.NavigationGraph
import com.ribuufing.findlostitem.ui.theme.FindLostItemTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSystemBars()
        setContent {
            FindLostItemTheme {
                MainContent()
            }
        }
    }

    private fun setupSystemBars() {
        // System bar'ları içerikten ayırmak için
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Status bar ve navigation bar'ı şeffaf yap
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // Status bar ve navigation bar görünürlüğü için ışık teması (light mode) ayarı
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true // Light mode için status bar
        insetsController.isAppearanceLightNavigationBars = true // Light mode için navigation bar
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun MainContent() {
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
                NavigationGraph(navController = navController) { isVisible ->
                    buttonsVisible = isVisible
                }
            }
        }
    }
}

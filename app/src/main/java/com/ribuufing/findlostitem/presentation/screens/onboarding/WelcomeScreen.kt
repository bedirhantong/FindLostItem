package com.ribuufing.findlostitem.presentation.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ribuufing.findlostitem.navigation.Routes

@Composable
fun WelcomeScreen(navController: NavHostController) {
    val imageUrl = remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Firebase Storage reference
    LaunchedEffect(Unit) {
        val storageRef = Firebase.storage
            .getReference("onboarding/")

        // Belirtilen klasördeki tüm dosyaları listeleme
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                if (listResult.items.isNotEmpty()) {
                    listResult.items[0].downloadUrl.addOnSuccessListener { uri ->
                        imageUrl.value = uri.toString()
                        isLoading = false
                    }
                } else {
                    isLoading = false
                }
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoading) {
            // Yükleniyor göstergesi veya placeholder göster
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Loading...")
            }
        } else {
            imageUrl.value?.let { url ->
                ImageScreen(imageUrl = url, navController = navController)
            } ?: Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Failed to load image.")
            }
        }
    }
}


@Composable
fun ImageScreen(imageUrl: String, navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
        ) {
            val painter: Painter = rememberAsyncImagePainter(model = imageUrl)
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Helping Each Other Find What’s Lost",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp).align(Alignment.BottomCenter)
            )
        }
        Column(
            modifier = Modifier
                .background(Color(0xFFF5F5F5), RoundedCornerShape(topEnd = 5.dp, topStart = 5.dp))
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
            ) {
            // Buraya Text, Icon gibi diğer öğeler ekleyebilirsiniz
            Text(
                text = "We're all in this together. Let's help each other find lost items.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D171C),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "We want to create a culture of trust and cooperation at our university. Our community is here to help you find your lost items, and we hope you will do the same for others.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF0D171C),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFED822B)

                ),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    navController.navigate(Routes.Login.route) // Navigate to Signup screen
                },
            ) {
                Text(text = "Get Started", color = Color.White)
            }

        }
    }
}
package com.ribuufing.findlostitem.presentation.screens.nointernet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ribuufing.findlostitem.R

@Composable
fun NoInternetScreen(openDialog: MutableState<Boolean>, onRetry: () -> Unit) {
    if (openDialog.value) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NoInternetImage()
                    Spacer(modifier = Modifier.height(20.dp))
                    NoInternetText()
                    Spacer(modifier = Modifier.height(24.dp))
                    RetryButton(onRetry)
                }
            }
        }
    }
}

@Composable
private fun NoInternetImage() {
    Image(
        painter = painterResource(id = R.drawable.no_intrenet),
        contentDescription = "No internet",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun NoInternetText() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Whoops!!",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 2.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No Internet connection was found. Check your connection or try again.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RetryButton(onRetry: () -> Unit) {
    Button(
        onClick = onRetry,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Try Again", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
    }
}

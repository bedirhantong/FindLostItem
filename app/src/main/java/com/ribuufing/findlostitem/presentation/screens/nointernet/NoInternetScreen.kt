package com.ribuufing.findlostitem.presentation.screens.nointernet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .wrapContentSize()
                    .border(
                        border = BorderStroke(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
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
            .padding(16.dp) // Görüntüye daha fazla boşluk
    )
}

@Composable
private fun NoInternetText() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Whoops!",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No Internet connection found.\nPlease check your connection or try again.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f) // Hafif daha koyu
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun RetryButton(onRetry: () -> Unit) {
    Button(
        onClick = onRetry,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("Try Again", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}


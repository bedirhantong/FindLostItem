package com.ribuufing.findlostitem.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ribuufing.findlostitem.navigation.Routes
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class OnboardingPage(
    val imageUrl: String = "",
    val title: String,
    val description: String,
    val backgroundColor: Color = Color(0xFFF5F5F5)
)

@Composable
fun WelcomeScreen(navController: NavHostController) {
    var onboardingPages by remember { mutableStateOf<List<OnboardingPage>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        val storageRef = Firebase.storage.reference.child("onboarding")
        
        try {
            val result = storageRef.listAll().await()
            val pages = mutableListOf<OnboardingPage>()
            
            result.items.forEachIndexed { index, item ->
                val url = item.downloadUrl.await().toString()
                pages.add(
                    when(index) {
                        0 -> OnboardingPage(
                            imageUrl = url,
                            title = "Lost Something?",
                            description = "Don't worry! Our community is here to help you find your lost items around the campus.",
                            backgroundColor = Color(0xFFFFF3E0)
                        )
                        1 -> OnboardingPage(
                            imageUrl = url,
                            title = "Found Something?",
                            description = "Be a hero! Help others find their lost belongings by reporting what you've found.",
                            backgroundColor = Color(0xFFE3F2FD)
                        )
                        else -> OnboardingPage(
                            imageUrl = url,
                            title = "Campus Community",
                            description = "Join our trusted network of students helping each other. Together, we make our campus a better place!",
                            backgroundColor = Color(0xFFE8F5E9)
                        )
                    }
                )
            }
            onboardingPages = pages
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFED822B))
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPage(onboardingPages[page])
        }

        TextButton(
            onClick = { 
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Welcome.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                "Skip",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == iteration) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == iteration)
                                    Color(0xFFED822B)
                                else
                                    Color(0xFFED822B).copy(alpha = 0.5f)
                            )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = pagerState.currentPage > 0,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ) {
                        Text("Back", color = Color(0xFFED822B))
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < pagerState.pageCount - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            navController.navigate(Routes.Paywall.route) {
                                popUpTo(Routes.Welcome.route) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFED822B)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 140.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pagerState.pageCount - 1) 
                            "Get Started" 
                        else 
                            "Next",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(page.backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = page.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(0.4f)
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF0D171C),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color(0xFF0D171C).copy(alpha = 0.8f)
            )
        }
    }
}
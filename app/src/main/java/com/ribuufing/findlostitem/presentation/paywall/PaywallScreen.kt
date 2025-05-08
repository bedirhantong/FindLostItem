package com.ribuufing.findlostitem.presentation.paywall

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ribuufing.findlostitem.navigation.Routes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.border


data class SubscriptionOption(
    val title: String,
    val price: String,
    val duration: String,
    val savings: String? = null,
    val features: List<String>
)

@Composable
fun PaywallScreen(navController: NavHostController) {
    var selectedPlan by remember { mutableStateOf(1) }

    val colors = PaywallColors(
        background = Color(0xFFF8FAFF),
        cardBackground = Color.White,
        primary = Color(0xFF4A90E2),
        secondary = Color(0xFF50E3C2),
        accent = Color(0xFFFF9500),
        text = Color(0xFF1F2937),
        textSecondary = Color(0xFF6B7280),
        buttonGradient = Brush.horizontalGradient(listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))),
        gradient = listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedMapHeader(colors)
            Spacer(modifier = Modifier.height(32.dp))
            TitleSection(colors)
            Spacer(modifier = Modifier.height(32.dp))
            SuccessStats(colors)
            Spacer(modifier = Modifier.height(32.dp))
            SubscriptionPlans(selectedPlan, colors) { selectedPlan = it }
            Spacer(modifier = Modifier.height(32.dp))
            ActionButton(navController, colors)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AnimatedMapHeader(colors: PaywallColors) {

    val infiniteTransition = rememberInfiniteTransition()
    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        AsyncImage(
            model = "https://static3.depositphotos.com/1007784/240/v/950/depositphotos_2409034-stock-illustration-washington-dc.jpg",
            contentDescription = "Map Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colors.background.copy(alpha = 0.6f),
                            colors.background
                        )
                    )
                )
        )

        AnimatedMarkers(colors)

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 180.dp)
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(1000, easing = EaseInOut)
                )
                .scale(scaleAnimation),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "FIND YOUR",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))
                    ),
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = Offset(4f, 4f),
                        blurRadius = 10f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "LOST ITEMS",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))
                    ),
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.7f),
                        offset = Offset(6f, 6f),
                        blurRadius = 15f
                    )
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AnimatedMarkers(colors: PaywallColors) {
    val markerLocations = listOf(
        Pair(0.2f, 2.6f),
        Pair(0.8f, 1.2f),
        Pair(2.5f, 2.6f),
        Pair(2.2f, 0.7f),
        Pair(0.5f, 0.9f)
    )


    markerLocations.forEachIndexed { index, (x, y) ->
        AnimatedMarker(
            x = x,
            y = y,
            delay = index * 500,
            colors = colors
        )
    }
}

@Composable
fun AnimatedMarker(x: Float, y: Float, delay: Int, colors: PaywallColors) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, delayMillis = delay, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
//            .fillMaxSize()
            .padding(top = (100 * y).dp, start = (x * 100).dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(pulseAnimation)
                .background(
                    color = colors.primary.copy(alpha = 0.15f),
                    shape = CircleShape
                )
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = colors.primary.copy(alpha = 0.9f),
                    shape = CircleShape
                )
                .border(2.dp, colors.secondary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Marker",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}


@Composable
fun TitleSection(colors: PaywallColors) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "ITEM GUARDIAN",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(colors.primary, colors.secondary)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Never Lose Track Again",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colors.text
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join thousands of people who found their lost items",
            fontSize = 18.sp,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SuccessStats(colors: PaywallColors) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatItem(value = "93", title = "Success Rate", icon = Icons.Default.Star, colors = colors)
        StatItem(value = "24", title = "Avg. Find Time", icon = Icons.Default.Menu, colors = colors)
        StatItem(value = "50K", title = "Items Found", icon = Icons.Default.CheckCircle, colors = colors)
    }
}

@Composable
fun SubscriptionPlans(selectedPlan: Int, colors: PaywallColors, onSelect: (Int) -> Unit) {
    val plans = listOf(
        SubscriptionOption(
            title = "Basic Plan",
            price = "$9.99",
            duration = "month",
            features = listOf("Unlimited Tracking", "24/7 Support")
        ),
        SubscriptionOption(
            title = "Premium Plan",
            price = "$19.99",
            duration = "month",
            savings = "Save 20%",
            features = listOf("Unlimited Tracking", "24/7 Support", "Priority Support", "Advanced Features")
        )
    )

    Column {
        plans.forEachIndexed { index, plan ->
            SubscriptionCard(
                option = plan,
                isSelected = selectedPlan == index + 1,
                onSelect = { onSelect(index + 1) },
                colors = colors
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun SubscriptionCard(
    option: SubscriptionOption,
    isSelected: Boolean,
    colors: PaywallColors,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .selectable(selected = isSelected, onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, colors.primary) else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = option.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    if (option.savings != null) {
                        Text(
                            text = option.savings,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(
                                    colors.accent,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = option.price,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Text(
                        text = "per ${option.duration}",
                        fontSize = 14.sp,
                        color = colors.textSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            option.features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        fontSize = 16.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(navController: NavHostController, colors: PaywallColors) {
    Button(
        onClick = { navController.navigate(Routes.Login.route) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.buttonGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Start Protection Now",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
fun StatItem(value: String, title: String, icon: ImageVector, colors: PaywallColors) {
    Card(
        modifier = Modifier.padding(8.dp).width(110.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = colors.textSecondary
            )
        }
    }
}

data class PaywallColors(
    val background: Color,
    val cardBackground: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val text: Color,
    val textSecondary: Color,
    val buttonGradient: Brush,
    val gradient : List<Color>
)
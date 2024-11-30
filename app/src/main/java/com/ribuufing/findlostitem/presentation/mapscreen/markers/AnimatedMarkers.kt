package com.ribuufing.findlostitem.presentation.mapscreen.markers

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.presentation.mapscreen.Cluster

@Composable
fun AnimatedSingleMarker(
    item: LostItem,
    onClick: () -> Unit
) {
    val isAnimating by rememberUpdatedState(true)
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    SingleItemMarker(
        item = item,
        onClick = {
            onClick()
            true
        },
        modifier = Modifier.scale(scale)
    )
}

@Composable
fun AnimatedClusterMarker(
    cluster: Cluster,
    onClick: () -> Unit
) {
    val isAnimating by rememberUpdatedState(true)
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    ClusterMarker(
        cluster = cluster,
        onClick = {
            onClick()
            true
        },
        modifier = Modifier.scale(scale)
    )
} 
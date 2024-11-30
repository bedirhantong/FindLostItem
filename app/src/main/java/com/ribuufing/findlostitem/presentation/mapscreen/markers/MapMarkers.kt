package com.ribuufing.findlostitem.presentation.mapscreen.markers

import android.content.Context
import android.graphics.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.presentation.mapscreen.Cluster

@Composable
fun ClusterMarker(
    cluster: Cluster,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val markerIcon = remember(cluster.items.size) {
        createClusterMarkerBitmap(
            context = context,
            clusterSize = cluster.items.size,
            backgroundColor = getClusterColor(cluster.items.size)
        )
    }

    Marker(
        state = rememberMarkerState(position = LatLng(cluster.center.latitude, cluster.center.longitude)),
        onClick = { onClick(); true },
        icon = BitmapDescriptorFactory.fromBitmap(markerIcon),
        title = "${cluster.items.size} items"
    )
}

@Composable
fun SingleItemMarker(
    item: LostItem,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val markerIcon = remember {
        createSingleMarkerBitmap(
            context = context,
            backgroundColor = getMarkerColor(item.numOfUpVotes - item.numOfDownVotes)
        )
    }

    Marker(
        state = rememberMarkerState(position = LatLng(item.deliverLatLng.latitude, item.deliverLatLng.longitude)),
        onClick = { onClick(); true },
        icon = BitmapDescriptorFactory.fromBitmap(markerIcon),
        title = item.itemName
    )
}

private fun getClusterColor(size: Int): Color {
    return when {
        size > 10 -> Color(0xFFD32F2F)
        size > 5 -> Color(0xFFF57C00)
        size > 3 -> Color(0xFFFBC02D)
        else -> Color(0xFF388E3C)
    }
}

private fun getMarkerColor(rating: Int): Color {
    return when {
        rating > 10 -> Color(0xFFD32F2F)
        rating > 5 -> Color(0xFFF57C00)
        rating > 3 -> Color(0xFFFBC02D)
        rating > 0 -> Color(0xFF388E3C)
        else -> Color(0xFF757575)
    }
} 
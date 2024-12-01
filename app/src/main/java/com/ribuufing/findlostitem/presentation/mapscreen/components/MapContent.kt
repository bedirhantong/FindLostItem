package com.ribuufing.findlostitem.presentation.mapscreen.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.presentation.mapscreen.Cluster
import com.ribuufing.findlostitem.presentation.mapscreen.MapViewModel
import com.ribuufing.findlostitem.presentation.mapscreen.markers.AnimatedClusterMarker
import com.ribuufing.findlostitem.presentation.mapscreen.markers.AnimatedSingleMarker

@Composable
fun MapContent(
    clusters: List<Cluster>,
    onMarkerClick: (Location) -> Unit,
    viewModel: MapViewModel,
    cameraPositionState: CameraPositionState
) {
    val akdenizUniversityBounds = listOf(
        LatLng(36.88647221734685, 30.662544051047327),
        LatLng(36.9008353387027, 30.66451987103039),
        LatLng(36.89975145334323, 30.63405932261706),
        LatLng(36.88761602348164, 30.638656677775707),
        LatLng(36.88832691917245, 30.655198176170995),
        LatLng(36.88647221734685, 30.662544051047327)
    )

    val campusCenter = LatLng(
        akdenizUniversityBounds.dropLast(1).map { it.latitude }.average(),
        akdenizUniversityBounds.dropLast(1).map { it.longitude }.average()
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(campusCenter, 14.5f)
    }

    LaunchedEffect(cameraPositionState.position) {
        viewModel.updateZoomLevel(cameraPositionState.position.zoom)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                LocalContext.current,
                R.raw.map_style
            )
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false
        )
    ) {
        Polygon(
            points = akdenizUniversityBounds,
            fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            strokeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            strokeWidth = 2f
        )

        clusters.forEach { cluster ->
            if (cluster.items.size == 1) {
                AnimatedSingleMarker(
                    item = cluster.items.first(),
                    onClick = { onMarkerClick(cluster.center) }
                )
            } else {
                AnimatedClusterMarker(
                    cluster = cluster,
                    onClick = { onMarkerClick(cluster.center) }
                )
            }
        }
    }
}
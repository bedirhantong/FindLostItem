package com.ribuufing.findlostitem.presentation.mapscreen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.presentation.mapscreen.components.MapBottomSheet
import com.ribuufing.findlostitem.presentation.mapscreen.components.MapTopAppBar
import com.ribuufing.findlostitem.presentation.mapscreen.markers.AnimatedClusterMarker
import com.ribuufing.findlostitem.presentation.mapscreen.markers.AnimatedSingleMarker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val clusters by viewModel.clusters.collectAsState()
    val selectedClusterItems by viewModel.selectedClusterItems.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()
    val isBottomSheetExpanded by viewModel.isBottomSheetExpanded.collectAsState()
    
    val cameraPositionState = rememberCameraPositionState()
    val scrollState = remember { mutableStateOf(0f) }

    LaunchedEffect(cameraPositionState.position) {
        scrollState.value = (cameraPositionState.position.bearing / 360f).coerceIn(0f, 1f)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        MapContent(
            clusters = clusters,
            onMarkerClick = viewModel::onMapClick,
            viewModel = viewModel,
            cameraPositionState = cameraPositionState
        )

        MapTopAppBar(
            navController = navController,
            scrollState = scrollState.value
        )

        if (isBottomSheetExpanded) {
            MapBottomSheet(
                isExpanded = isBottomSheetExpanded,
                selectedItem = selectedItem,
                selectedItems = selectedClusterItems,
                onItemClick = viewModel::selectItem,
                onDismiss = viewModel::collapseBottomSheet,
                onNavigateToDetail = { itemId -> 
                    navController.navigate("item_detail/$itemId")
                }
            )
        }
    }
}

@Composable
private fun MapContent(
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

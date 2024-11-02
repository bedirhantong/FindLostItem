package com.ribuufing.findlostitem.presentation.screens.mapscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import kotlin.random.Random
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.Manifest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val clusters by viewModel.clusters.collectAsState()
    val selectedClusterItems by viewModel.selectedClusterItems.collectAsState()
    val isBottomSheetExpanded by viewModel.isBottomSheetExpanded.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val coroutineScope = rememberCoroutineScope()

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(36.896405, 30.658459), 15f)
    }

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    LaunchedEffect(permissionState) {
        if (permissionState.allPermissionsGranted) {
            getCurrentLocation(
                fusedLocationClient,
                onGetCurrentLocationSuccess = { location ->
                    userLocation = LatLng(location.first, location.second)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)
                },
                onGetCurrentLocationFailed = {
                    // Handle location retrieval failure
                }
            )
        } else {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    if (permissionState.allPermissionsGranted) {
        Scaffold { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = MapProperties(isMyLocationEnabled = true),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        mapToolbarEnabled = false,
                        myLocationButtonEnabled = true
                    ),
                    onMapClick = { latLng ->
                        viewModel.onMapClick(Location(latLng.latitude, latLng.longitude))
                    }
                ) {
                    clusters.forEach { cluster ->
                        Circle(
                            center = LatLng(cluster.center.latitude, cluster.center.longitude),
                            radius = 500.0 * cluster.items.size,
                            fillColor = getDensityColor(cluster.items.size).copy(alpha = 0.5f),
                            strokeColor = Color.Transparent,
                            clickable = true,
                            onClick = {
                                viewModel.onMapClick(cluster.center)
                                coroutineScope.launch {
                                    bottomSheetState.show()
                                }
                            }
                        )
                    }
                }

                if (isBottomSheetExpanded) {
                    ModalBottomSheet(
                        onDismissRequest = { viewModel.collapseBottomSheet() },
                        sheetState = bottomSheetState,
                        dragHandle = { BottomSheetDefaults.DragHandle() }
                    ) {
                        BottomSheetContent(
                            items = selectedClusterItems,
                            onItemClick = { item ->
                                navController.navigate("item_detail/${item.id}")
                            }
                        )
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Location permission is required to use this feature")
            Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                Text("Request Permission")
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onGetCurrentLocationSuccess: (Pair<Double, Double>) -> Unit,
    onGetCurrentLocationFailed: (Exception) -> Unit,
    priority: Boolean = true
) {
    val accuracy = if (priority) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY

    fusedLocationClient.getCurrentLocation(accuracy, null)
        .addOnSuccessListener { location ->
            location?.let {
                onGetCurrentLocationSuccess(Pair(it.latitude, it.longitude))
            }
        }
        .addOnFailureListener { exception ->
            onGetCurrentLocationFailed(exception)
        }
}


@Composable
fun BottomSheetContent(
    items: List<LostItem>,
    onItemClick: (LostItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Items found",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(items) { item ->
                LostItemCard(
                    item = item,
                    onItemClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun LostItemCard(
    item: LostItem,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.images.firstOrNull(),
                contentDescription = item.title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${calculateDistance(item.foundWhere)} km",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "View Details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun calculateDistance(location: Location): String {
    // Implement distance calculation logic here
    return String.format("%.1f", Random.nextDouble(0.1, 5.0))
}

fun getDensityColor(density: Int): Color {
    return when {
        density > 3 -> Color(0xFFB71C1C)
        density > 2 ->Color(0xFFFFA000)
        else -> Color(0xFF388E3C)
    }
}


//@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
//@Composable
//fun MapScreen(viewModel: MapViewModel = hiltViewModel(), navController: NavHostController) {
//    val clusters by viewModel.clusters.collectAsState()
//    val isBottomSheetExpanded by viewModel.isBottomSheetExpanded.collectAsState()
//    val selectedClusterItems by viewModel.selectedClusterItems.collectAsState()
//
//    // Request Location Permission
//    val locationPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            // Enable location features if granted
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//    }
//
//    val cameraPositionState = rememberCameraPositionState {
//        // Set initial camera position here if needed
//    }
//
//    // Create the sheet state for the modal bottom sheet
//    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
//    // Get a coroutine scope for launching the hide operation
//    val coroutineScope = rememberCoroutineScope()
//
//    // Modal Bottom Sheet Layout
//    ModalBottomSheetLayout(
//        sheetState = sheetState,
//        sheetContent = {
//            BottomSheetContent(
//                items = selectedClusterItems,
//                onDismissRequest = {
//                    viewModel.collapseBottomSheet()
//                    coroutineScope.launch { sheetState.hide() }
//                }
//            )
//        }
//    ) {
//        // Main Map with Cluster Circles and Tap Listener
//        GoogleMap(
//            modifier = Modifier.fillMaxSize(),
//            properties = MapProperties(isMyLocationEnabled = true),
//            cameraPositionState = cameraPositionState,
//            uiSettings = MapUiSettings(
//                zoomControlsEnabled = true,
//                mapToolbarEnabled = false
//            ),
//            onMapClick = { latLng ->
//                val location = Location(latitude = latLng.latitude, longitude = latLng.longitude)
//                viewModel.onMapClick(location)
//            }
//        ) {
//            // Draw heatmap for each cluster
//            clusters.forEach { cluster ->
//                Circle(
//                    center = LatLng(cluster.center.latitude, cluster.center.longitude),
//                    radius = 300.0 * cluster.items.size,
//                    fillColor = getDensityColor(cluster.items.size).copy(alpha = 0.5f),
//                    strokeColor = Color.Transparent
//                )
//            }
//        }
//    }
//
//    // Manage the bottom sheet visibility based on isBottomSheetExpanded
//    LaunchedEffect(isBottomSheetExpanded) {
//        if (isBottomSheetExpanded) {
//            sheetState.show()  // Show the bottom sheet
//        } else {
//            sheetState.hide()  // Hide the bottom sheet
//        }
//    }
//}
//
//@Composable
//fun BottomSheetContent(items: List<LostItem>, onDismissRequest: () -> Unit) {
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text(
//            text = "Lost Items",
//            style = MaterialTheme.typography.displayMedium
//        )
//        Button(onClick = onDismissRequest) {
//            Text("Close")
//        }
//        LazyColumn {
//            items(items) { item ->
//                LostItemRow(item)
//            }
//        }
//    }
//}
//
//@Composable
//fun LostItemRow(item: LostItem) {
//    Column(modifier = Modifier.padding(vertical = 8.dp)) {
//        Text(text = item.title, style = MaterialTheme.typography.bodySmall)
//        // Add more details about the item if necessary
//        Text(text = "Description: ${item.description}", style = MaterialTheme.typography.bodySmall)
//    }
//}
//
// Helper function to get color based on density
//fun getDensityColor(density: Int): Color {
//    return when {
//        density > 10 -> Color(0xFFB71C1C) // High density: red
//        density > 5 -> Color(0xFFFFA000) // Medium density: orange
//        else -> Color(0xFF388E3C) // Low density: green
//    }
//}



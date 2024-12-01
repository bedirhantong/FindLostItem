package com.ribuufing.findlostitem.presentation.mapscreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.maps.android.compose.*
import com.ribuufing.findlostitem.presentation.mapscreen.components.MapBottomSheet
import com.ribuufing.findlostitem.presentation.mapscreen.components.MapContent
import com.ribuufing.findlostitem.presentation.mapscreen.components.MapTopAppBar

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
    val scrollState = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(cameraPositionState.position) {
        scrollState.floatValue = (cameraPositionState.position.bearing / 360f).coerceIn(0f, 1f)
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
            scrollState = scrollState.floatValue
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

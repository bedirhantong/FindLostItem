package com.ribuufing.findlostitem.presentation.lostitemdetail.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.data.model.LostItem

@Composable
fun ItemLocationMap(
    item: LostItem,
    modifier: Modifier = Modifier
) {
    val itemLocation = LatLng(
        item.deliverLatLng.latitude,
        item.deliverLatLng.longitude
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(itemLocation, 16f)
    }

    GoogleMap(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16/9f)
            .clip(RoundedCornerShape(12.dp)),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                LocalContext.current,
                R.raw.map_style
            )
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
            zoomGesturesEnabled = false,
            scrollGesturesEnabled = false,
            rotationGesturesEnabled = false,
            tiltGesturesEnabled = false
        )
    ) {
        Marker(
            state = rememberMarkerState(position = itemLocation),
            title = item.itemName
        )
    }
} 
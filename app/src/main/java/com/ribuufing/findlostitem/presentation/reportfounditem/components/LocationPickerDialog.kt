package com.ribuufing.findlostitem.presentation.reportfounditem.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LocationPickerDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (latitude: Double, longitude: Double) -> Unit,
    initialLocation: LatLng? = null
) {
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Choose a Location") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    onMapClick = { latLng ->
                        if (isInsideCampus(latLng)) {
                            selectedLocation = latLng
                        } else {
                            Toast.makeText(
                                context,
                                "This location is outside the campus area and cannot be selected.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            selectedLocation ?: LatLng(36.8964124764409, 30.64975069784397),
                            14f
                        )
                    },
                    properties = MapProperties()
                ) {
                    CampusPolygon()
                    selectedLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Selected Location",
                            snippet = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedLocation?.let {
                        onLocationSelected(it.latitude, it.longitude)
                        onDismiss()
                    }
                },
                enabled = selectedLocation != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun CampusPolygon() {
    Polygon(
        points = listOf(
            LatLng(36.88647221734685, 30.662544051047327),
            LatLng(36.9008353387027, 30.66451987103039),
            LatLng(36.89975145334323, 30.63405932261706),
            LatLng(36.88761602348164, 30.638656677775707),
            LatLng(36.88832691917245, 30.655198176170995)
        ),
        fillColor = Color(0x3300FF00),
        strokeColor = Color(0xFF00FF00),
        strokeWidth = 2f
    )
}

fun isInsideCampus(latLng: LatLng): Boolean {
    return latLng.latitude in 36.88647221734685..36.9008353387027 &&
            latLng.longitude in 30.63405932261706..30.66451987103039
} 
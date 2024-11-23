package com.ribuufing.findlostitem.presentation.reportfounditem

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ribuufing.findlostitem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundItemScreen() {
    var itemName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var foundWhere by remember { mutableStateOf("") }
    var placedWhere by remember { mutableStateOf("") }
    val selectImages = remember { mutableStateListOf<Uri>() }
    var foundLatLng by remember { mutableStateOf<LatLng?>(null) }
    var deliverLatLng by remember { mutableStateOf<LatLng?>(null) }
    var showLocationPickerType by remember { mutableStateOf<LocationPickerType?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            selectImages.clear()
            selectImages.addAll(uris)
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details of found item") },
                actions = {
                    val isFormValid = itemName.isNotBlank() && message.isNotBlank() &&
                            foundWhere.isNotBlank() && placedWhere.isNotBlank()

                    IconButton(
                        enabled = isFormValid,
                        onClick = {
                            // Gönderim işlemleri burada yapılabilir
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                CustomTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = "Item name",
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(5.dp))

                CustomTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = "Message",
                    leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = null) }


                )

                Spacer(modifier = Modifier.height(5.dp))

                CustomTextField(
                    value = foundWhere,
                    onValueChange = { foundWhere = it },
                    label = "Where did you find it?",
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.from_icon),
                            contentDescription = "Found Location icon",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { showLocationPickerType = LocationPickerType.FOUND },
                            modifier = Modifier.background(
                                if (foundLatLng != null) Color.Green else Color.Gray,
                                shape = RoundedCornerShape(50)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Choose location",
                                tint = Color.White
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(5.dp))

                CustomTextField(
                    value = placedWhere,
                    onValueChange = { placedWhere = it },
                    label = "Where did you deliver it?",
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.placed_icon),
                            contentDescription = "Placed Location icon",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { showLocationPickerType = LocationPickerType.PLACED },
                            modifier = Modifier.background(
                                if (deliverLatLng != null) Color.Green else Color.Gray,
                                shape = RoundedCornerShape(50)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Choose location",
                                tint = Color.White
                            )
                        }
                    }
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(200.dp)
                                .clickable { galleryLauncher.launch("image/*") }
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
                                .background(Color(0xFFF2EDE8), shape = RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Image",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp)
                            )
                        }
                    }

                    items(selectImages) { uri ->
                        Box(modifier = Modifier.padding(8.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(200.dp)
                            )
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove Image",
                                tint = Color.Red,
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(50)
                                    )
                                    .size(24.dp)
                                    .padding(4.dp)
                                    .clickable {
                                        selectImages.remove(uri)
                                    }
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }

            showLocationPickerType?.let { pickerType ->
                LocationPickerDialog(
                    onDismiss = { showLocationPickerType = null },
                    onLocationSelected = { latitude, longitude ->
                        if (pickerType == LocationPickerType.FOUND) {
                            foundLatLng = LatLng(latitude, longitude)
                        } else {
                            deliverLatLng = LatLng(latitude, longitude)
                        }
                        showLocationPickerType = null
                    },
                    initialLocation = if (pickerType == LocationPickerType.FOUND) foundLatLng else deliverLatLng
                )
            }
        }
    )
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        shape = RoundedCornerShape(10.dp),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = Color(0xFFF2EDE8),
            focusedBorderColor = Color(0xFFED822B),
            unfocusedBorderColor = Color(0x708B8B8B),
            focusedLabelColor = Color(0xFFED822B),
            cursorColor = Color(0xFF99704D),
            textColor = Color(0xFF99704D)
        )
    )
}

@Composable
fun LocationPickerDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (latitude: Double, longitude: Double) -> Unit,
    initialLocation: LatLng? = null
) {
    var selectedLocation by remember { mutableStateOf(initialLocation) }

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
                        selectedLocation = latLng
                    },
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            selectedLocation ?: LatLng(36.8964124764409, 30.64975069784397),
                            14f
                        )
                    }
                ) {
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

enum class LocationPickerType {
    FOUND, PLACED
}





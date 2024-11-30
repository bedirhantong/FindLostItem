package com.ribuufing.findlostitem.presentation.reportfounditem

import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.ribuufing.findlostitem.R
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundItemScreen(
    viewModel: ReportFoundItemViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    navController: NavHostController
) {
    val itemName by viewModel.itemName.collectAsState()
    val message by viewModel.message.collectAsState()
    val foundWhere by viewModel.foundWhere.collectAsState()
    val placedWhere by viewModel.placedWhere.collectAsState()
    val selectedImages by viewModel.selectedImages.collectAsState()
    val foundLatLng by viewModel.foundLatLng.collectAsState()
    val deliverLatLng by viewModel.deliverLatLng.collectAsState()
    val sendStatus by viewModel.sendStatus.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()

    var showLocationPickerType by remember { mutableStateOf<LocationPickerType?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            viewModel.updateSelectedImages(uris)
        }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(sendStatus) {
        when (sendStatus) {
            SendStatus.Sending -> {
                snackbarHostState.showSnackbar("Gönderiliyor...")
            }

            SendStatus.Success -> {
                snackbarHostState.showSnackbar("Gönderildi!")
                onNavigateToHome()
            }

            SendStatus.Error -> {
                snackbarHostState.showSnackbar("Gönderim başarısız!")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Post item") },
                actions = {
                    IconButton(
                        enabled = isFormValid,
                        onClick = {
                            viewModel.submitReport()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                    onValueChange = { viewModel.updateItemName(it) },
                    label = "Item name",
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(5.dp))

                CustomTextField(
                    value = message,
                    onValueChange = { viewModel.updateMessage(it) },
                    label = "Message",
                    leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(5.dp))

                CustomTextField(
                    value = foundWhere,
                    onValueChange = { viewModel.updateFoundWhere(it) },
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
                    onValueChange = { viewModel.updatePlacedWhere(it) },
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

                    items(selectedImages) { uri ->
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
                                        viewModel.removeImage(uri)
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
                            viewModel.updateFoundLocation(LatLng(latitude, longitude))
                        } else {
                            viewModel.updateDeliverLocation(LatLng(latitude, longitude))
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
                    properties = MapProperties(
                        //mapStyleOptions = MapStyleOptions(mapStyle)
                    )
                ) {
                    // Add a green polygon representing the campus area
                    Polygon(
                        points = listOf(
                            LatLng(36.88647221734685, 30.662544051047327),
                            LatLng(36.9008353387027, 30.66451987103039),
                            LatLng(36.89975145334323, 30.63405932261706),
                            LatLng(36.88761602348164, 30.638656677775707),
                            LatLng(36.88832691917245, 30.655198176170995)
                        ),
                        fillColor = Color(0x3300FF00),  // Green fill color with 20% opacity
                        strokeColor = Color(0xFF00FF00), // Green stroke color
                        strokeWidth = 2f
                    )


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

fun isInsideCampus(latLng: LatLng): Boolean {
    // Check if the selected location is within the new campus bounds
    return latLng.latitude in 36.88647221734685..36.9008353387027 &&
            latLng.longitude in 30.63405932261706..30.66451987103039
}

enum class LocationPickerType {
    FOUND, PLACED
}


enum class SendStatus {
    Idle,
    Sending,
    Success,
    Error
}


fun uploadImagesToStorage(
    images: List<Uri>,
    itemId: String,
    onUploadComplete: (List<String>) -> Unit,
    onUploadFailed: (Exception) -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val uploadedImageUrls = mutableListOf<String>()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"


    images.forEachIndexed { index, imageUri ->
        val imageRef =
            storageRef.child("images/items-by-user/$userId/$itemId/${itemId}_${index}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    uploadedImageUrls.add(downloadUri.toString())

                    if (uploadedImageUrls.size == images.size) {
                        onUploadComplete(uploadedImageUrls)
                    }
                }
            }
            .addOnFailureListener { exception ->
                onUploadFailed(exception)
            }
    }
}


fun sendToFirestoreWithImages(
    itemName: String,
    message: String,
    foundWhere: String,
    placedWhere: String,
    foundLatLng: LatLng?,
    deliverLatLng: LatLng?,
    images: List<Uri>,
    onComplete: (Boolean) -> Unit // Gönderim tamamlandığında çağrılacak

) {
    val itemId = UUID.randomUUID().toString() // Benzersiz itemId oluştur

    uploadImagesToStorage(
        images,
        itemId, // Yeni parametre olarak itemId geçiriliyor
        onUploadComplete = { imageUrls ->
            val db = FirebaseFirestore.getInstance()

            val data = hashMapOf(
                "itemId" to itemId,
                "senderInfo" to hashMapOf(
                    "senderId" to FirebaseAuth.getInstance().currentUser?.uid,
                    "email" to FirebaseAuth.getInstance().currentUser?.email
                ),
                "timestamp" to FieldValue.serverTimestamp(),
                "itemName" to itemName,
                "message" to message,
                "foundWhere" to foundWhere,
                "placedWhere" to placedWhere,
                "foundLatLng" to foundLatLng,
                "deliverLatLng" to deliverLatLng,
                "images" to imageUrls, // Resim URL'leri
                "is_picked" to false,
                "numOfDownVotes" to 0,
                "numOfUpVotes" to 0
            )

            db.collection("found_items_test")
                .document(itemId)
                .set(data)
                .addOnSuccessListener {
                    onComplete(true)
                    Log.d("Firestore", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    onComplete(false)
                    Log.d("Firestore", "Error writing document", e)
                }
        },
        onUploadFailed = { exception ->
            onComplete(false)
            Log.d("Storage", "Error uploading images", exception)
        }
    )
}






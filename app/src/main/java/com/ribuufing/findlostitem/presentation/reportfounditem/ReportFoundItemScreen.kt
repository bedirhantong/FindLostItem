package com.ribuufing.findlostitem.presentation.reportfounditem

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.presentation.reportfounditem.components.ImageSection
import com.ribuufing.findlostitem.presentation.reportfounditem.components.LocationField
import com.ribuufing.findlostitem.presentation.reportfounditem.components.LocationPickerDialog

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
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        viewModel.updateSelectedImages(uris)
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(sendStatus) {
        when (sendStatus) {
            SendStatus.Success -> {
                onNavigateToHome()
                snackbarHostState.showSnackbar("Posted successfully!")
            }
            SendStatus.Error -> {
                snackbarHostState.showSnackbar("Failed to post")
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("New Post") },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            enabled = sendStatus != SendStatus.Sending
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        Button(
                            onClick = { viewModel.submitReport() },
                            enabled = isFormValid && sendStatus != SendStatus.Sending,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFED822B),
                                disabledContainerColor = Color(0xFFED822B).copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            if (sendStatus == SendStatus.Sending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Share")
                            }
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                ImageSection(
                    selectedImages = selectedImages,
                    onAddImage = { galleryLauncher.launch("image/*") },
                    onRemoveImage = { viewModel.removeImage(it) }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { viewModel.updateItemName(it) },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFED822B),
                            focusedLabelColor = Color(0xFFED822B),
                            cursorColor = Color(0xFFED822B)
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { viewModel.updateMessage(it) },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFED822B),
                            focusedLabelColor = Color(0xFFED822B),
                            cursorColor = Color(0xFFED822B)
                        )
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF2EDE8)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            LocationField(
                                value = foundWhere,
                                onValueChange = { viewModel.updateFoundWhere(it) },
                                label = "Found Location",
                                icon = painterResource(id = R.drawable.from_icon),
                                hasLocation = foundLatLng != null,
                                onLocationClick = { showLocationPickerType = LocationPickerType.FOUND }
                            )

                            LocationField(
                                value = placedWhere,
                                onValueChange = { viewModel.updatePlacedWhere(it) },
                                label = "Placed Location",
                                icon = painterResource(id = R.drawable.placed_icon),
                                hasLocation = deliverLatLng != null,
                                onLocationClick = { showLocationPickerType = LocationPickerType.PLACED }
                            )
                        }
                    }
                }
            }
        }

        if (sendStatus == SendStatus.Sending) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color(0xFFED822B),
                    strokeWidth = 4.dp
                )
            }
        }

        showLocationPickerType?.let { pickerType ->
            LocationPickerDialog(
                onDismiss = { 
                    if (sendStatus != SendStatus.Sending) {
                        showLocationPickerType = null 
                    }
                },
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
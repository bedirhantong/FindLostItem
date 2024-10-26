package com.ribuufing.findlostitem.presentation.screens.reportfounditem

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
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
import com.ribuufing.findlostitem.data.model.LostItem
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import com.ribuufing.findlostitem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundItemScreen() {
    var itemName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var foundWhere by remember { mutableStateOf("") }
    var placedWhere by remember { mutableStateOf("") }
    var images by remember { mutableStateOf<List<String>>(emptyList()) }
    val selectImages = remember { mutableStateListOf<Uri>() }

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
                    IconButton(onClick = {
                        val lostItem = LostItem(
                            title = itemName,
                            description = message,
                            images = images,
                            foundWhere = foundWhere,
                            placedWhere = placedWhere
                        )
                        // onSubmit(lostItem)
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
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
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { androidx.compose.material.Text("Item name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFF2EDE8),
                        focusedBorderColor = Color(0xFFED822B),
                        unfocusedBorderColor = Color(0x708B8B8B),
                        focusedLabelColor = Color(0xFFED822B),
                        cursorColor = Color(0xFF99704D),
                        textColor = Color(0xFF99704D)
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { androidx.compose.material.Text("Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = null) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFF2EDE8),
                        focusedBorderColor = Color(0xFFED822B),
                        unfocusedBorderColor = Color(0x708B8B8B),
                        focusedLabelColor = Color(0xFFED822B),
                        cursorColor = Color(0xFF99704D),
                        textColor = Color(0xFF99704D)
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = foundWhere,
                    onValueChange = { foundWhere = it },
                    label = { androidx.compose.material.Text("Where did you find it?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.from_icon),
                            contentDescription = "Found Location icon",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFF2EDE8),
                        focusedBorderColor = Color(0xFFED822B),
                        unfocusedBorderColor = Color(0x708B8B8B),
                        focusedLabelColor = Color(0xFFED822B),
                        cursorColor = Color(0xFF99704D),
                        textColor = Color(0xFF99704D)
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = placedWhere,
                    onValueChange = { placedWhere = it },
                    label = { androidx.compose.material.Text("Where did you deliver it?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.placed_icon),
                            contentDescription = "Placed Location icon",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF6E2425)
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFF2EDE8),
                        focusedBorderColor = Color(0xFFED822B),
                        unfocusedBorderColor = Color(0x708B8B8B),
                        focusedLabelColor = Color(0xFFED822B),
                        cursorColor = Color(0xFF99704D),
                        textColor = Color(0xFF99704D)
                    )
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                ) {
                    // İlk olarak "Add Image" butonunu gösteriyoruz
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

                    // Sonra seçilen fotoğrafları gösteriyoruz
                    items(selectImages) { uri ->
                        Box(modifier = Modifier.padding(8.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clickable {
                                        images =
                                            images
                                                .toMutableList()
                                                .apply { add(uri.toString()) }
                                    }
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
                                        images = selectImages.map { it.toString() }
                                    }
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }
        }
    )
}



package com.ribuufing.findlostitem.presentation.screens.reportfounditem

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.material.*
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundItemScreen() {
    var itemName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var foundWhere by remember { mutableStateOf("") }
    var placedWhere by remember { mutableStateOf("") }
    var images by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectImages by remember { mutableStateOf(listOf<Uri>()) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            selectImages = it
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details of found item") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
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
                    .background(Color(0xFFFFF8F1))
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
                        unfocusedBorderColor = Color(0x00686868),
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
                        unfocusedBorderColor = Color(0x00686868),
                        focusedLabelColor = Color(0xFFED822B),
                        cursorColor = Color(0xFF99704D),
                        textColor = Color(0xFF99704D)
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))


                OutlinedTextField(
                    value = foundWhere,
                    onValueChange = { foundWhere = it },
                    label = { Text("Where did you find it?") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = placedWhere,
                    onValueChange = { placedWhere = it },
                    label = { Text("Where did you deliver it?") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                )

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(10.dp)
                ) {
                    Text(text = "Pick Image From Gallery")
                }

                Spacer(modifier = Modifier.height(5.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(selectImages) { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentScale = ContentScale.FillWidth,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(100.dp)
                                .clickable { }
                        )
                    }
                }
            }
        }
    )
}


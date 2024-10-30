package com.ribuufing.findlostitem.presentation.screens.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ribuufing.findlostitem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(navController: NavHostController, scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Macbook Air",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Last seen: 1/15 at 2pm, 5th floor of library",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back"
                )
            }
        },
        scrollBehavior = scrollBehavior

    )
}
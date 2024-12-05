package com.ribuufing.findlostitem.presentation.screens.profile.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.data.model.LostItem

@Composable
fun LostItemRow(item: LostItem) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            val painter = rememberAsyncImagePainter(model = item.images[0])
            val painterState = painter.state

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .padding(end = 16.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )


                if (painterState is AsyncImagePainter.State.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp)
                    )
                }


                if (painterState is AsyncImagePainter.State.Error) {
                    Text(
                        text = "Failed to load",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }


            Column(
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                )


                Text(
                    text = "Lost on ${item.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }


        IconButton(
            onClick = {

            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.trash),
                contentDescription = "Delete item",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF000000)
            )
        }
    }
}
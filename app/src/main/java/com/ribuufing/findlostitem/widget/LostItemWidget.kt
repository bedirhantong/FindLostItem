package com.ribuufing.findlostitem.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.ribuufing.findlostitem.data.model.LostItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class LostItemWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val lastLostItem = fetchLastLostItem()
        Log.d("LostItemWidget", "Fetched LostItem: $lastLostItem")

        provideContent {
            GlanceTheme {
                MyContent(lastLostItem, context)
            }
        }
    }

    @Composable
    private fun MyContent(lastLostItem: LostItem? = null, context: Context) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            lastLostItem?.let {
                // Display title
                Text(
                    text = it.title.ifEmpty { "No title available" },
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
                // Display description
                Text(
                    text = it.description.ifEmpty { "No description available" },
                    style = TextStyle(fontSize = 14.sp),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
                // Found and placed locations
                Text(
                    text = "Found at: ${it.foundWhere.ifEmpty { "Unknown location" }}",
                    style = TextStyle(fontSize = 14.sp),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Placed at: ${it.placedWhere.ifEmpty { "Unknown location" }}",
                    style = TextStyle(fontSize = 14.sp),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
                // Display date
                Text(
                    text = "Date: ${it.date.ifEmpty { "No date available" }}",
                    style = TextStyle(fontSize = 12.sp),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
            } ?: run {
                Text(text = "No items found", style = TextStyle(fontSize = 14.sp))
            }

            // Button to go back to the main activity
            Button(
                text = "Go to Home",
                onClick = {
                    // Navigate to home
                }
            )
        }
    }
}

private suspend fun fetchLastLostItem(): LostItem? {
    val firestore = Firebase.firestore
    return withContext(Dispatchers.IO) {
        try {
            val lostItems = firestore.collection("lost_items")
                .get()
                .await()
                .toObjects(LostItem::class.java)
            lostItems.lastOrNull()
        } catch (e: Exception) {
            Log.e("LostItemWidget", "Error fetching lost items: ${e.message}")
            null
        }
    }
}





package com.ribuufing.findlostitem.domain.repository

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.utils.Result

interface LostItemRepository {
    suspend fun getLostItems(): List<LostItem>
    suspend fun upvoteLostItem(itemId: String, currentUpvotes: Int)
    suspend fun downvoteLostItem(itemId: String, currentDownvotes: Int)
    suspend fun getLostItemsInArea(location: Location, radius: Double): List<LostItem>
    suspend fun getLostItemById(itemId: String): LostItem
    suspend fun getLostItemsByUserId(userId: String): List<LostItem>
    suspend fun uploadFoundItem(
        itemName: String,
        message: String,
        foundWhere: String,
        placedWhere: String,
        foundLatLng: LatLng,
        deliverLatLng: LatLng,
        images: List<Uri>
    ): Result<Unit>
}

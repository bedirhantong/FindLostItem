package com.ribuufing.findlostitem.data

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.ribuufing.findlostitem.data.datasources.FirestoreDataSource
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import com.ribuufing.findlostitem.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class LostItemRepositoryImpl @Inject constructor(
    private val dataSource: FirestoreDataSource,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : LostItemRepository {
    override suspend fun getLostItems(): List<LostItem> {
        return dataSource.fetchLostItems()
    }

    override suspend fun upvoteLostItem(itemId: String, currentUpvotes: Int) {
        val newUpvotes = currentUpvotes + 1
        dataSource.updateLostItemField(itemId, "numOfUpVotes", newUpvotes)
    }

    override suspend fun downvoteLostItem(itemId: String, currentDownvotes: Int) {
        val newDownvotes = currentDownvotes + 1
        dataSource.updateLostItemField(itemId, "numOfDownVotes", newDownvotes)
    }

    override suspend fun getLostItemsInArea(location: Location, radius: Double): List<LostItem> = withContext(Dispatchers.IO) {
        dataSource.getLostItemsInArea(location, radius)
    }

    override suspend fun getLostItemById(itemId: String): LostItem {
        return dataSource.getLostItemById(itemId)
    }

    override suspend fun getLostItemsByUserId(userId: String): List<LostItem> {
        return dataSource.getLostItemsByUserId(userId)
    }

    override suspend fun uploadFoundItem(
        itemName: String,
        message: String,
        foundWhere: String,
        placedWhere: String,
        foundLatLng: LatLng,
        deliverLatLng: LatLng,
        images: List<Uri>
    ): Result<Unit> = try {
        val itemId = UUID.randomUUID().toString()
        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        val userEmail = auth.currentUser?.email ?: throw Exception("User email not found")
        
        val imageUrls = dataSource.uploadImages(images, itemId, userId)
        
        dataSource.uploadFoundItem(
            itemId = itemId,
            itemName = itemName,
            message = message,
            foundWhere = foundWhere,
            placedWhere = placedWhere,
            foundLatLng = foundLatLng,
            deliverLatLng = deliverLatLng,
            images = imageUrls,
            userId = userId,
            userEmail = userEmail
        )
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Failure(e)
    }

}

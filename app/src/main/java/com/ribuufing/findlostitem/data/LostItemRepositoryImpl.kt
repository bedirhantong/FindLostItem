package com.ribuufing.findlostitem.data

import com.ribuufing.findlostitem.data.datasources.FirestoreDataSource
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LostItemRepositoryImpl @Inject constructor(
    private val dataSource: FirestoreDataSource
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

}

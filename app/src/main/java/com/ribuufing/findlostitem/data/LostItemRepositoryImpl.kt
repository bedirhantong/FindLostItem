package com.ribuufing.findlostitem.data

import com.ribuufing.findlostitem.data.datasources.FirestoreDataSource
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import javax.inject.Inject

class LostItemRepositoryImpl @Inject constructor(
    private val dataSource: FirestoreDataSource
) : LostItemRepository {
    override suspend fun getLostItems(): List<LostItem> {
        return dataSource.fetchLostItems()
    }

    override suspend fun addDummyData() {
        dataSource.addDummyData()
    }

    override suspend fun upvoteLostItem(itemId: String, currentUpvotes: Int) {
        val newUpvotes = currentUpvotes + 1
        dataSource.updateLostItemField(itemId, "numOfUpVotes", newUpvotes)
    }

    override suspend fun downvoteLostItem(itemId: String, currentDownvotes: Int) {
        val newDownvotes = currentDownvotes + 1
        dataSource.updateLostItemField(itemId, "numOfDownVotes", newDownvotes)
    }

    override suspend fun getLostItemById(itemId: String): LostItem {
        return dataSource.getLostItemById(itemId)
    }
}

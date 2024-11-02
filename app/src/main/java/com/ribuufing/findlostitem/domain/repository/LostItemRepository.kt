package com.ribuufing.findlostitem.domain.repository

import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem

interface LostItemRepository {
    suspend fun getLostItems(): List<LostItem>
    suspend fun addDummyData()
    suspend fun upvoteLostItem(itemId: String, currentUpvotes: Int)
    suspend fun downvoteLostItem(itemId: String, currentDownvotes: Int)
    suspend fun getLostItemsInArea(location: Location, radius: Double): List<LostItem>
    suspend fun getLostItemById(itemId: String): LostItem
}

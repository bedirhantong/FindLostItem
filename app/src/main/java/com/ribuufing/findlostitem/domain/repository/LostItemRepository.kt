package com.ribuufing.findlostitem.domain.repository

import com.ribuufing.findlostitem.data.model.LostItem

interface LostItemRepository {
    suspend fun getLostItems(): List<LostItem>
    suspend fun addDummyData()
    suspend fun upvoteLostItem(itemId: String, currentUpvotes: Int)
    suspend fun downvoteLostItem(itemId: String, currentDownvotes: Int)
}

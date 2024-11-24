package com.ribuufing.findlostitem.data

import com.ribuufing.findlostitem.data.datasources.FirestoreDataSource
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import com.ribuufing.findlostitem.utils.Result
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

    override suspend fun getAllChatByUserUid(userUid: String): List<Chat> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatByChatId(chatId: String): Result<Chat?> {
        TODO("Not yet implemented")
    }

    override suspend fun createChat(senderUid: String, receiverUid: String): Result<Chat> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessageToChat(chatId: String, message: Message): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getChat(senderUid: String, receiverUid: String): Result<Chat?> {
        TODO("Not yet implemented")
    }

    override suspend fun getMessages(chatId: String): Result<List<Message>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllChatsForUser(userUid: String): List<Chat> {
        TODO("Not yet implemented")
    }


}

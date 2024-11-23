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
        return dataSource.getAllChatByUserUid(userUid)
    }

    override suspend fun getChatByChatId(chatId: String): Result<Chat?> {
        return try {
            val chat = dataSource.getChatByChatId(chatId) // Firestore'dan veri çekiliyor
            Result.Success(chat)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


    override suspend fun createChat(senderUid: String, receiverUid: String): Result<Chat> = withContext(Dispatchers.IO) {
        try {
            val chat = dataSource.createChat(senderUid, receiverUid)
            Result.Success(chat)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun sendMessageToChat(chatId: String, message: Message): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val isSuccessful = dataSource.addMessageToChat(chatId, message)
            Result.Success(isSuccessful)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getChat(senderUid: String, receiverUid: String): Result<Chat?> = withContext(Dispatchers.IO) {
        try {
            val chat = dataSource.getChat(senderUid, receiverUid)
            Result.Success(chat)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getMessages(chatId: String): Result<List<Message>> = withContext(Dispatchers.IO) {
        try {
            val messages = dataSource.getMessagesForChat(chatId)
            Result.Success(messages)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getAllChatsForUser(userUid: String): List<Chat> = withContext(Dispatchers.IO) {
        dataSource.getAllChatsForUser(userUid)
    }

}

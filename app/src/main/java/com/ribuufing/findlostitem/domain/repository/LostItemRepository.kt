package com.ribuufing.findlostitem.domain.repository

import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.utils.Result

interface LostItemRepository {
    suspend fun getLostItems(): List<LostItem>
    suspend fun upvoteLostItem(itemId: String, currentUpvotes: Int)
    suspend fun downvoteLostItem(itemId: String, currentDownvotes: Int)
    suspend fun getLostItemsInArea(location: Location, radius: Double): List<LostItem>
    suspend fun getLostItemById(itemId: String): LostItem
    suspend fun getAllChatByUserUid(userUid : String) : List<Chat>
    suspend fun getChatByChatId(chatId: String): Result<Chat?>

    suspend fun createChat(senderUid: String, receiverUid: String): Result<Chat>
    suspend fun sendMessageToChat(chatId: String, message: Message): Result<Boolean>
    suspend fun getChat(senderUid: String, receiverUid: String): Result<Chat?>
    suspend fun getMessages(chatId: String): Result<List<Message>>

    suspend fun getAllChatsForUser(userUid: String): List<Chat>
}

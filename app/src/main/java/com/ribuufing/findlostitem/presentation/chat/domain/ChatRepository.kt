package com.ribuufing.findlostitem.presentation.chat.domain

import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatsForUser(currentUserId: String): Flow<List<Chat>>
    fun getOrCreateChat(itemId: String, currentUserId: String, otherUserId: String): Flow<Chat>
    fun getChatMessages(chatId: String): Flow<List<Message>>
    suspend fun sendMessage(chatId: String, message: Message)
}

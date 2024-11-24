package com.ribuufing.findlostitem.presentation.chat.domain

import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    fun getChatsForUser(currentUserId: String): Flow<List<Chat>> {
        return chatRepository.getChatsForUser(currentUserId)
    }

    fun getOrCreateChat(itemId: String, currentUserId: String, otherUserId: String): Flow<Chat> {
        return chatRepository.getOrCreateChat(itemId, currentUserId, otherUserId)
    }

    fun getChatMessages(chatId: String): Flow<List<Message>> {
        return chatRepository.getChatMessages(chatId)
    }

    suspend fun sendMessage(chatId: String, senderId: String, content: String) {
        val message = Message(
            chatId = chatId,
            senderId = senderId,
            content = content
        )
        chatRepository.sendMessage(chatId, message)
    }
}

package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.ribuufing.findlostitem.utils.Result
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val chatRepository: LostItemRepository
) {

    suspend fun createOrGetChat(senderUid: String, receiverUid: String) = flow {
        emit(Result.Loading)
        try {
            val existingChat = chatRepository.getChat(senderUid, receiverUid)
            if (existingChat is Result.Success && existingChat.data != null) {
                emit(Result.Success(existingChat.data))
            } else {
                val newChatResult = chatRepository.createChat(senderUid, receiverUid)
                emit(newChatResult)
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    // Create a new chat
    suspend fun createChat(senderUid: String, receiverUid: String) = flow {
        try {
            emit(Result.Loading)
            val chatResult = chatRepository.createChat(senderUid, receiverUid)
            if (chatResult is Result.Success) {
                emit(Result.Success(chatResult.data))
            } else {
                emit(Result.Failure((chatResult as Result.Failure).exception))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    // Get an existing chat
    suspend fun getChat(senderUid: String, receiverUid: String) = flow {
        try {
            emit(Result.Loading)
            val chatResult = chatRepository.getChat(senderUid, receiverUid)
            if (chatResult is Result.Success) {
                emit(Result.Success(chatResult.data))  // Correctly emit Chat or null
            } else {
                emit(Result.Failure((chatResult as Result.Failure).exception))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    // Send a message to an existing chat
    suspend fun sendMessageToChat(chatId: String, message: Message) = flow {
        try {
            emit(Result.Loading)
            val chatResult = chatRepository.sendMessageToChat(chatId, message)
            if (chatResult is Result.Success) {
                emit(Result.Success(chatResult.data))  // Correctly emit Chat or null
            } else {
                emit(Result.Failure((chatResult as Result.Failure).exception))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    // Örnek düzenlenmiş `getMessages` fonksiyonu
    suspend fun getMessages(chatId: String) = flow {
        try {
            emit(Result.Loading)
            if (chatId.isEmpty()) {
                emit(Result.Failure(IllegalArgumentException("Chat ID cannot be empty.")))
                return@flow
            }
            val chatResult = chatRepository.getMessages(chatId)
            if (chatResult is Result.Success) {
                emit(Result.Success(chatResult.data))  // Correctly emit Chat or null
            } else {
                emit(Result.Failure((chatResult as Result.Failure).exception))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

}
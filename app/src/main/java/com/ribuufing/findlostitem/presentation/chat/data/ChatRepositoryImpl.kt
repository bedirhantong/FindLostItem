package com.ribuufing.findlostitem.presentation.chat.data

import com.ribuufing.findlostitem.data.datasources.FirestoreDataSource
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.presentation.chat.domain.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val dataSource: FirestoreDataSource
) : ChatRepository {

    // Kullanıcıya ait sohbetleri almak
    override fun getChatsForUser(currentUserId: String): Flow<List<Chat>> = callbackFlow {
        val listener = dataSource.getChatsForUser(currentUserId)
            .onEach { chats ->
                trySend(chats).isSuccess
            }.catch { error ->
                close(error)
            }.launchIn(this)

        awaitClose { listener.cancel() }
    }

    override fun getOrCreateChat(itemId: String, currentUserId: String, otherUserId: String): Flow<Chat> = callbackFlow {
        val listener = dataSource.getOrCreateChat(itemId, currentUserId, otherUserId)
            .onEach { chat ->
                trySend(chat).isSuccess
            }.catch { error ->
                close(error)
            }.launchIn(this)

        awaitClose { listener.cancel() }
    }

    override fun getChatMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = dataSource.getChatMessages(chatId)
            .onEach { messages ->
                trySend(messages).isSuccess
            }.catch { error ->
                close(error)
            }.launchIn(this)

        awaitClose { listener.cancel() }
    }

    override suspend fun sendMessage(chatId: String, message: Message) {
        dataSource.sendMessage(chatId, message)
    }
}

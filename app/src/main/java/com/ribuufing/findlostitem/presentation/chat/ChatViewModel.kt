package com.ribuufing.findlostitem.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.presentation.chat.domain.ChatUseCase
import com.ribuufing.findlostitem.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase
) : ViewModel() {

    private val _chatState = MutableStateFlow<Result<Chat>>(Result.Loading)
    val chatState: StateFlow<Result<Chat>> = _chatState

    private val _messagesState = MutableStateFlow<Result<List<Message>>>(Result.Loading)
    val messagesState: StateFlow<Result<List<Message>>> = _messagesState

    private var currentChatId: String? = null

    fun createOrGetChat(itemId: String, currentUserId: String, otherUserId: String) {
        viewModelScope.launch {
            chatUseCase.getOrCreateChat(itemId, currentUserId, otherUserId)
                .onStart { _chatState.value = Result.Loading }
                .catch { exception -> _chatState.value = Result.Failure(exception) }
                .collect { chat ->
                    _chatState.value = Result.Success(chat)
                    currentChatId = chat.id
                    getMessages(chat.id)
                }
        }
    }

    fun sendMessage(senderId: String, content: String) {
        viewModelScope.launch {
            val chatId = currentChatId
            if (chatId != null) {
                try {
                    chatUseCase.sendMessage(chatId, senderId, content)
                    getMessages(chatId)
                } catch (exception: Throwable) {
                    _messagesState.value = Result.Failure(exception)
                }
            } else {
                _messagesState.value = Result.Failure(IllegalStateException("Chat ID is null"))
            }
        }
    }

    private fun getMessages(chatId: String) {
        viewModelScope.launch {
            chatUseCase.getChatMessages(chatId)
                .onStart { _messagesState.value = Result.Loading }
                .catch { exception -> _messagesState.value = Result.Failure(exception) }
                .collect { messages ->
                    _messagesState.value = Result.Success(messages)
                }
        }
    }
}
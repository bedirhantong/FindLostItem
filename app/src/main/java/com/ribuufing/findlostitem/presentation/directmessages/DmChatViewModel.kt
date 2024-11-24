package com.ribuufing.findlostitem.presentation.directmessages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.domain.use_cases.GetCurrentUserUidUseCase
import com.ribuufing.findlostitem.presentation.chat.domain.ChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DmChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase
) : ViewModel() {

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchChats()
    }

    private fun fetchChats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserId = getCurrentUserUidUseCase.invoke()
                chatUseCase.getChatsForUser(currentUserId)
                    .collect {
                        _chats.value = it
                    }
            } catch (e: Exception) {
                // Hata durumunda loglama yapılabilir
            } finally {
                _isLoading.value = false
            }
        }
    }
}


@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId

    init {
        fetchCurrentUserId()
    }

    private fun fetchCurrentUserId() {
        viewModelScope.launch {
            _currentUserId.value = getCurrentUserUidUseCase.invoke()
        }
    }

    fun fetchMessagesForChat(chatId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                chatUseCase.getChatMessages(chatId).collect { messages ->
                    _messages.value = messages
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(chatId: String, content: String) {
        viewModelScope.launch {
            try {
                val currentUserId = _currentUserId.value
                chatUseCase.sendMessage(chatId, currentUserId, content)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

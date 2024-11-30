package com.ribuufing.findlostitem.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.domain.use_cases.GetLostItemByIdUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetUserInfosByUidUseCase
import com.ribuufing.findlostitem.presentation.chat.domain.ChatUseCase
import com.ribuufing.findlostitem.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,
    private val getLostItemByIdUseCase: GetLostItemByIdUseCase,
    private val getUserInfosByUidUseCase: GetUserInfosByUidUseCase
) : ViewModel() {

    private val _chatState = MutableStateFlow<Result<Chat>>(Result.Loading)
    val chatState: StateFlow<Result<Chat>> = _chatState

    private val _messagesState = MutableStateFlow<Result<List<Message>>>(Result.Loading)
    val messagesState: StateFlow<Result<List<Message>>> = _messagesState

    private val _lostItemState = MutableStateFlow<Result<LostItem>>(Result.Loading)
    val lostItemState: StateFlow<Result<LostItem>> = _lostItemState

    private var currentChatId: String? = null

    private val userCache = mutableMapOf<String, User>()

    fun createOrGetChat(itemId: String, currentUserId: String, otherUserId: String) {
        viewModelScope.launch {
            try {
                chatUseCase.getOrCreateChat(itemId, currentUserId, otherUserId).collect { chat ->
                    _chatState.value = Result.Success(chat)
                    currentChatId = chat.id
                    loadMessages(chat.id)
                    loadLostItem(itemId)
                }
            } catch (e: Exception) {
                _chatState.value = Result.Failure(e)
            }
        }
    }

    private fun loadLostItem(itemId: String) {
        viewModelScope.launch {
            try {
                getLostItemByIdUseCase(itemId).collect { lostItem ->
                    _lostItemState.value = Result.Success(lostItem)
                }
            } catch (e: Exception) {
                _lostItemState.value = Result.Failure(e)
            }
        }
    }

    private fun loadMessages(chatId: String) {
        viewModelScope.launch {
            try {
                chatUseCase.getChatMessages(chatId).collect { messages ->
                    _messagesState.value = Result.Success(messages.sortedByDescending { it.timestamp })
                }
            } catch (e: Exception) {
                _messagesState.value = Result.Failure(e)
            }
        }
    }

    fun sendMessage(senderId: String, content: String) {
        viewModelScope.launch {
            currentChatId?.let { chatId ->
                try {
                    chatUseCase.sendMessage(chatId, senderId, content)
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    fun getUserImage(userId: String): String? {
        return userCache[userId]?.imageUrl ?: run {
            fetchUserInfo(userId)
            null
        }
    }

    fun getUserName(userId: String): String? {
        return userCache[userId]?.name ?: run {
            fetchUserInfo(userId)
            null
        }
    }

    private fun fetchUserInfo(userId: String) {
        if (!userCache.containsKey(userId)) {
            viewModelScope.launch {
                try {
                    getUserInfosByUidUseCase(userId).collect { result ->
                        if (result is Result.Success) {
                            result.data?.let { user ->
                                userCache[userId] = user
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Error fetching user info: ${e.message}")
                }
            }
        }
    }
}
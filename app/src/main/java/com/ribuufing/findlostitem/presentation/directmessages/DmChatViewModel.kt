package com.ribuufing.findlostitem.presentation.directmessages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.domain.use_cases.GetCurrentUserUidUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetUserInfosByUidUseCase
import com.ribuufing.findlostitem.presentation.chat.domain.ChatUseCase
import com.ribuufing.findlostitem.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatWithUser(
    val chat: Chat,
    val otherUser: User,
    val itemId: String
)

@HiltViewModel
class DmChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,
    private val getUserInfosByUidUseCase: GetUserInfosByUidUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase
) : ViewModel() {

    private val _chatWithUsers = MutableStateFlow<List<ChatWithUser>>(emptyList())
    val chatWithUsers: StateFlow<List<ChatWithUser>> = _chatWithUsers

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    var currentUserId = ""
    private val processedChats = mutableSetOf<String>()

    init {
        Log.d("DmChatViewModel", "ViewModel initialized")
        fetchChatsWithUsers()
    }

    private fun fetchChatsWithUsers() {
        viewModelScope.launch {
            try {
                currentUserId = getCurrentUserUidUseCase.invoke()
                Log.d("DmChatViewModel", "Current user ID: $currentUserId")

                chatUseCase.getChatsForUser(currentUserId).collect { chats ->
                    Log.d("DmChatViewModel", "Received ${chats.size} chats")
                    
                    if (chats.isEmpty()) {
                        _isLoading.value = false
                        return@collect
                    }

                    val tempList = mutableListOf<ChatWithUser>()
                    var completedUsers = 0

                    chats.forEach { chat ->
                        if (!processedChats.contains(chat.id)) {
                            Log.d("DmChatViewModel", "Processing chat: ${chat.id}")
                            val otherUserId = chat.participants.find { it != currentUserId }
                            Log.d("DmChatViewModel", "Other user ID: $otherUserId")

                            otherUserId?.let { uid ->
                                try {
                                    getUserInfosByUidUseCase(uid).collect { result ->
                                        when (result) {
                                            is Result.Success -> {
                                                result.data?.let { user ->
                                                    Log.d("DmChatViewModel", "Found user: ${user.name}")
                                                    tempList.add(
                                                        ChatWithUser(
                                                            chat = chat,
                                                            otherUser = user,
                                                            itemId = chat.itemId
                                                        )
                                                    )
                                                    processedChats.add(chat.id)
                                                }
                                                completedUsers++
                                                if (completedUsers == chats.size) {
                                                    _chatWithUsers.value = tempList.sortedByDescending { 
                                                        it.chat.lastMessageTimestamp.toDate() 
                                                    }
                                                    _isLoading.value = false
                                                    Log.d("DmChatViewModel", "All users processed, loading complete")
                                                }
                                            }
                                            is Result.Failure -> {
                                                Log.e("DmChatViewModel", "Error getting user info: ${result.exception.message}")
                                                completedUsers++
                                            }
                                            is Result.Loading -> {
                                                Log.d("DmChatViewModel", "Loading user info")
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("DmChatViewModel", "Error collecting user info: ${e.message}")
                                    completedUsers++
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("DmChatViewModel", "Error fetching chats: ${e.message}", e)
                _isLoading.value = false
            }
        }
    }
}
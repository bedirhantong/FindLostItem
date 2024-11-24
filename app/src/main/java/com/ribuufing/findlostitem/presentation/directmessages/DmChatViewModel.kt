package com.ribuufing.findlostitem.presentation.directmessages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.Chat
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

    var currentUserId = ""
    init {
        fetchChats()
    }

    private fun fetchChats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                currentUserId = getCurrentUserUidUseCase.invoke()
                chatUseCase.getChatsForUser(currentUserId)
                    .collect { chats ->
                        _chats.value = chats
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Log.d("DmChatViewModel", "Error: ${e.localizedMessage}")
                _isLoading.value = false
            }
        }
    }
}
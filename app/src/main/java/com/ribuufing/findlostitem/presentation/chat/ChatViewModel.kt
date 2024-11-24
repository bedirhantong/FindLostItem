package com.ribuufing.findlostitem.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.domain.use_cases.GetCurrentUserUidUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetLostItemByIdUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetUserInfosByUidUseCase
import com.ribuufing.findlostitem.presentation.chat.domain.ChatUseCase
import com.ribuufing.findlostitem.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
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
                    getMessages(chatId) // Mesaj gönderildikten sonra mesajları yenile
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



//@HiltViewModel
//class ChatViewModel @Inject constructor(
//    private val getUserInfosByUidUseCase: GetUserInfosByUidUseCase,
//    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
//    private val chatUseCase: ChatUseCase
//) : ViewModel() {
//
//    fun createOrGetChat(receiverUid: String) {
//        val senderUid = getCurrentUserUid()
//        viewModelScope.launch {
//            chatUseCase.createOrGetChat(senderUid, receiverUid).collect { result ->
//                when (result) {
//                    is Result.Success -> {
//                        chatId = result.data.id
//                        if (chatId!!.isNotBlank()) {
//                            getMessages(chatId!!)
//                        } else {
//                            Log.e("ChatViewModel", "Chat has an empty ID")
//                        }
//                    }
//                    is Result.Failure -> {
//                        Log.e("ChatViewModel", "Failed to create or get chat: ${result.exception}")
//                    }
//                    is Result.Loading -> {
//                        _isLoading.value = true
//                    }
//                }
//            }
//        }
//    }
//
//    private val _messages = MutableStateFlow<List<Message>>(emptyList())
//    val messages: StateFlow<List<Message>> = _messages
//
//    private val _senderUserInfos = MutableStateFlow<Result<User?>>(Result.Loading)
//    val senderUserInfos: StateFlow<Result<User?>> = _senderUserInfos
//
//    private val _receiverUserInfos = MutableStateFlow<Result<User?>>(Result.Loading)
//    val receiverUserInfos: StateFlow<Result<User?>> = _receiverUserInfos
//
//    private val _isLoading = MutableStateFlow(true)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _sendMessageState = MutableStateFlow<Result<Boolean?>>(Result.Loading)
//    val sendMessageState: StateFlow<Result<Boolean?>> get() = _sendMessageState
//
//    private var chatId: String? = null
//
//    // Refresh user information
//    fun refreshUserInfos(receiverUid: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            getUserInfosByUid(getCurrentUserUid(), receiverUid)
//            _isLoading.value = false
//        }
//    }
//
//    fun getCurrentUserUid(): String {
//        return getCurrentUserUidUseCase.invoke()
//    }
//
//    // Get user information for sender and receiver
//    private fun getUserInfosByUid(senderUid: String, receiverUid: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                getUserInfosByUidUseCase(senderUid).collect { result ->
//                    _senderUserInfos.value = result
//                }
//
//                getUserInfosByUidUseCase(receiverUid).collect { result ->
//                    _receiverUserInfos.value = result
//                }
//            } catch (e: Exception) {
//                _senderUserInfos.value = Result.Failure(e)
//                _receiverUserInfos.value = Result.Failure(e)
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//
//    fun createChat(receiverUid: String) {
//        val senderUid = getCurrentUserUid()
//        viewModelScope.launch {
//            chatUseCase.createChat(senderUid, receiverUid).collect { result ->
//                if (result is Result.Success) {
//                    chatId = result.data.id
//                    getMessages(chatId!!)
//                } else if (result is Result.Failure) {
//                    Log.e("ChatViewModel", "Failed to create chat: ${result.exception}")
//                }
//            }
//        }
//    }
//
//    fun sendMessage(message: Message) {
//        viewModelScope.launch {
//            chatId?.let { id ->
//                chatUseCase.sendMessageToChat(id, message).collect { result ->
//                    _sendMessageState.value = result
//                    if (result is Result.Success) {
//                        addMessage(message)
//                        Log.d("ChatViewModel", "Message Sent: ${message.content}")
//                    } else if (result is Result.Failure) {
//                        Log.e("ChatViewModel", "Failed to send message: ${result.exception}")
//                    }
//                }
//            } ?: Log.e("ChatViewModel", "Chat ID is null")
//        }
//    }
//
//    private fun getMessages(chatId: String) {
//        viewModelScope.launch {
//            chatUseCase.getMessages(chatId).collect { result ->
//                _isLoading.value = false
//                when (result) {
//                    is Result.Success -> {
//                        _messages.value = result.data
//                    }
//                    is Result.Failure -> {
//                        Log.e("ChatViewModel", "Failed to get messages: ${result.exception}")
//                        _messages.value = emptyList()
//                    }
//                    is Result.Loading -> {
//                        _messages.value = emptyList()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun addMessage(message: Message) {
//        _messages.update { currentMessages ->
//            currentMessages + message
//        }
//    }
//}
//

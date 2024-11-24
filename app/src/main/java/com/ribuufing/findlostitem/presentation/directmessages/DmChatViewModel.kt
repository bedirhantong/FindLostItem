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

@HiltViewModel
class DmChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,
    private val getUserInfosByUidUseCase: GetUserInfosByUidUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase
) : ViewModel() {

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _userInfos = MutableStateFlow<Result<User?>>(Result.Loading)
    val userInfos: StateFlow<Result<User?>> = _userInfos

    private val userCache = mutableMapOf<String, User?>()


    var currentUserId = ""
    init {
        fetchChats()
    }

    fun getUserInfosByUid(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (userCache.containsKey(uid)) {
                    _userInfos.value = Result.Success(userCache[uid])
                } else {
                    getUserInfosByUidUseCase(uid).collect { result ->
                        if (result is Result.Success) {
                            userCache[uid] = result.data
                        }
                        _userInfos.value = result
                    }
                }
            } catch (e: Exception) {
                _userInfos.value = Result.Failure(e)
            } finally {
                _isLoading.value = false
            }
        }
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
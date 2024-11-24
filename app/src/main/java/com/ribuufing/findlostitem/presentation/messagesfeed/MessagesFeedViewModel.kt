package com.ribuufing.findlostitem.presentation.messagesfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.domain.use_cases.GetAllChatsForUserUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetCurrentUserUidUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetUserInfosByUidUseCase
import com.ribuufing.findlostitem.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesFeedViewModel @Inject constructor(
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val getUserInfosByUidUseCase: GetUserInfosByUidUseCase,
    private val getAllChatsForUserUseCase: GetAllChatsForUserUseCase
) : ViewModel() {
    private val _allChats = MutableStateFlow<List<Chat>>(emptyList())
    val allChats: StateFlow<List<Chat>> = _allChats

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _userInfos = MutableStateFlow<Map<String, Result<User?>>>(emptyMap())
    val userInfos: StateFlow<Map<String, Result<User?>>> = _userInfos

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            val currentUserUid = getCurrentUserUidUseCase()
            getAllChatsForUserUseCase(currentUserUid).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _allChats.value = result.data
                        loadUserInfos(result.data)
                    }
                    is Result.Failure -> {
                        // Handle error
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    private fun loadUserInfos(chats: List<Chat>) {
        viewModelScope.launch {
//            val userUids = chats.flatMap { listOf(it.user1Id, it.user2Id) }.distinct()
//            userUids.forEach { uid ->
//                getUserInfosByUidUseCase(uid).collect { result ->
//                    _userInfos.update { currentMap ->
//                        currentMap + (uid to result)
//                    }
//                }
//            }
            _isLoading.value = false
        }
    }

    fun getOtherUserUid(chat: Chat): String {
//        val currentUserUid = getCurrentUserUidUseCase()
//        return if (chat.user1Id == currentUserUid) chat.user2Id else chat.user1Id
        return  ""
    }
}


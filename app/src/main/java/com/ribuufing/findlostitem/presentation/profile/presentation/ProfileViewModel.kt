package com.ribuufing.findlostitem.presentation.profile.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.domain.use_cases.GetCurrentUserUidUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetLostItemByIdUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetUserInfosByUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ribuufing.findlostitem.utils.Result

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserInfosByUidUseCase: GetUserInfosByUidUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val getLostItemByIdUseCase: GetLostItemByIdUseCase
) : ViewModel() {

    private val _userInfos = MutableStateFlow<Result<User?>>(Result.Loading)
    val userInfos: StateFlow<Result<User?>> = _userInfos

    private val _lostItems = MutableStateFlow<List<LostItem>>(emptyList())
    val lostItems: StateFlow<List<LostItem>> = _lostItems

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun refreshUserInfos() {
        viewModelScope.launch {
            _isLoading.value = true
            getUserInfosByUid()
            _isLoading.value = false
        }
    }

    private fun getCurrentUserUid(): String {
        return getCurrentUserUidUseCase.invoke()
    }

    private fun getUserInfosByUid() {
        val uid = getCurrentUserUid()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                getUserInfosByUidUseCase(uid).collect { result ->
                    _userInfos.value = result
                    if (result is Result.Success && result.data != null) {
                        loadLostItems(result.data.foundedItems)
                    }
                }
            } catch (e: Exception) {
                _userInfos.value = Result.Failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadLostItems(itemIds: List<String>) {
        viewModelScope.launch {
            val items = mutableListOf<LostItem>()
            for (id in itemIds) {
                getLostItemByIdUseCase(id).collect { item ->
                    item.let { items.add(it) }
                }
            }
            _lostItems.value = items
        }
    }
}

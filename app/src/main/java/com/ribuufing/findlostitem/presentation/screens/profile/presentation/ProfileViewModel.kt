package com.ribuufing.findlostitem.presentation.screens.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel  @Inject constructor() : ViewModel(){
    private val _userInfos = MutableStateFlow<Result<User>?>(null)
    val userInfos : StateFlow<Result<User>?> = _userInfos

    private val _isLoading = MutableStateFlow(true)  // İstek durumunu kontrol etmek için
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            // _userInfos.value = getUserInfosUseCase.invoke()
            _isLoading.value = false
        }
    }

    fun refreshUserInfos() {
        viewModelScope.launch {
            _isLoading.value = true
            // _userInfos.value = getUserInfosUseCase.invoke()
            _isLoading.value = false
        }
    }
}
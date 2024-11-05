package com.ribuufing.findlostitem.presentation.profile.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.domain.use_cases.GetCurrentUserUidUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetUserInfosByUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ribuufing.findlostitem.utils.Result

@HiltViewModel
class ProfileViewModel  @Inject constructor(
    private val getUserInfosByUidUseCase: GetUserInfosByUidUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase
) : ViewModel(){
    private val _userInfos = MutableStateFlow<Result<User?>>(Result.Loading)
    val userInfos : StateFlow<Result<User?>> = _userInfos


    private val _isLoading = MutableStateFlow(true)  // İstek durumunu kontrol etmek için
    val isLoading: StateFlow<Boolean> = _isLoading

    fun refreshUserInfos() {
        viewModelScope.launch {
            _isLoading.value = true
            getUserInfosByUid()
            _isLoading.value = false
        }
    }

    private fun getCurrentUserUid() : String {
        return getCurrentUserUidUseCase.invoke()
    }

    private fun getUserInfosByUid() {
        val uid = getCurrentUserUid()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("ProfileViewModel", "getUserInfosByUid: $uid")
                getUserInfosByUidUseCase(uid).collect { result ->
                    Log.d("ProfileViewModel", "getUserInfosByUid: $result")
                    _userInfos.value = result
                    Log.d("ProfileViewModel", "getUserInfosByUid: ${_userInfos.value}")
                }
            } catch (e: Exception) {
                _userInfos.value = Result.Failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
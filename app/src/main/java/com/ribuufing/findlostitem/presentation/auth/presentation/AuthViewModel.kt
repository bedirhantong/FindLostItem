package com.ribuufing.findlostitem.presentation.auth.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.ribuufing.findlostitem.presentation.auth.domain.usecase.LoginUserUseCase
import com.ribuufing.findlostitem.presentation.auth.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ribuufing.findlostitem.utils.Result

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _userState = MutableLiveData<Result<FirebaseUser?>>()
    val userState: LiveData<Result<FirebaseUser?>> = _userState

    fun registerUser(email: String, password: String, name: String) {
        viewModelScope.launch {
            _userState.value = Result.Loading
            try {
                registerUserUseCase(email, password, name).collect { result ->
                    _userState.value = result
                }
            } catch (e: Exception) {
                _userState.value = Result.Failure(e)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _userState.value = Result.Loading
            try {
                loginUserUseCase(email, password).collect { result ->
                    _userState.value = result
                }
            } catch (e: Exception) {
                _userState.value = Result.Failure(e)
            }
        }
    }
}



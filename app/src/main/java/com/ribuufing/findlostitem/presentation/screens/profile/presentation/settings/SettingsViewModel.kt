package com.ribuufing.findlostitem.presentation.screens.profile.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.presentation.screens.profile.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ribuufing.findlostitem.utils.Result

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _logoutState = MutableStateFlow<Result<Unit>?>(null)
    val logoutState: StateFlow<Result<Unit>?> = _logoutState

    private val _deleteAccountState = MutableStateFlow<Result<Unit>?>(null)
    val deleteAccountState: StateFlow<Result<Unit>?> = _deleteAccountState

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().collect { result ->
                _logoutState.value = result
            }
        }
    }

}

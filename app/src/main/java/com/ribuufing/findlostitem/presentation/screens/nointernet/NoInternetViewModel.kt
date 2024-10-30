package com.ribuufing.findlostitem.presentation.screens.nointernet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

@HiltViewModel
class NoInternetViewModel @Inject constructor(
    private val connectivityHelper: ConnectivityHelper
) : ViewModel() {

    val isInternetAvailable: MutableState<Boolean> = mutableStateOf(true)

    fun checkInternetConnection() {
        viewModelScope.launch {
            // Bağlantıyı her 5 saniyede bir kontrol et
            while (true) {
                isInternetAvailable.value = connectivityHelper.isInternetAvailable()
                delay(5000)
            }
        }
    }
}

package com.ribuufing.findlostitem.presentation.lostitemdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.use_cases.GetLostItemByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LostItemDetailViewModel @Inject constructor(
    private val getLostItemByIdUseCase: GetLostItemByIdUseCase
) : ViewModel() {
    private val _lostItem = MutableStateFlow<LostItem?>(null)
    val lostItem: StateFlow<LostItem?> = _lostItem

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getLostItemById(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getLostItemByIdUseCase(itemId).collect { item ->
                    _lostItem.value = item
                }
            } catch (e: Exception) {
                // Handle the error, log, or display a message to the user if necessary
                _lostItem.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}

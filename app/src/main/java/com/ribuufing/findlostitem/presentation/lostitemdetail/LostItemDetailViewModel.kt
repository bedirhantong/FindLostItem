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
    private val _lostItem = MutableStateFlow(LostItem())
    val lostItem = _lostItem

    private val _isLoading = MutableStateFlow(true)  // İstek durumunu kontrol etmek için
    val isLoading: StateFlow<Boolean> = _isLoading


    fun getLostItemById(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _lostItem.value = getLostItemByIdUseCase.invoke(itemId)
            _isLoading.value = false
        }
    }
}

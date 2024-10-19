package com.ribuufing.findlostitem.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.use_cases.DownVoteItemUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetLostItemsUseCase
import com.ribuufing.findlostitem.domain.use_cases.UpVoteItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LostItemsViewModel @Inject constructor(
    private val getLostItemsUseCase: GetLostItemsUseCase,
    private val upVoteItemUseCase: UpVoteItemUseCase,
    private val downVoteItemUseCase: DownVoteItemUseCase
) : ViewModel() {
    private val _lostItems = MutableStateFlow<List<LostItem>>(emptyList())
    val lostItems: StateFlow<List<LostItem>> = _lostItems

    private val _isLoading = MutableStateFlow(true)  // İstek durumunu kontrol etmek için
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
//        addDummyData()
        viewModelScope.launch {
            _isLoading.value = true
            _lostItems.value = getLostItemsUseCase.invoke()
            _isLoading.value = false
        }
    }

    fun upvoteItem(itemId: String, currentUpvotes: Int) {
        viewModelScope.launch {
            upVoteItemUseCase.invoke(itemId, currentUpvotes + 1)
        }
    }

    fun downVoteItem(itemId: String, currentDownvotes: Int) {
        viewModelScope.launch {
            downVoteItemUseCase.invoke(itemId, currentDownvotes - 1)
        }
    }

    private fun addDummyData() {
        viewModelScope.launch {
            getLostItemsUseCase.addDummyData()
        }
    }
}

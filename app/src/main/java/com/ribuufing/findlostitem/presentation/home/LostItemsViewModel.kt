package com.ribuufing.findlostitem.presentation.home

import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LostItemsViewModel @Inject constructor(
    private val getLostItemsUseCase: GetLostItemsUseCase,
    private val upVoteItemUseCase: UpVoteItemUseCase,
    private val downVoteItemUseCase: DownVoteItemUseCase
) : ViewModel() {
    private val _lostItems = MutableStateFlow<List<LostItem>>(emptyList())
    val lostItems: StateFlow<List<LostItem>> = _lostItems

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredLostItems: StateFlow<List<LostItem>> =
        combine(_lostItems, _searchQuery) { items, query ->
            filterLostItems(items, query)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchLostItems()
    }

    private fun fetchLostItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _lostItems.value = getLostItemsUseCase.invoke()
            _isLoading.value = false
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun filterLostItems(items: List<LostItem>, query: String): List<LostItem> {
        return items.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
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

    fun refreshLostItems() {
        fetchLostItems()
    }

    private fun addDummyData() {
        viewModelScope.launch {
            getLostItemsUseCase.addDummyData()
        }
    }
}


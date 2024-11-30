package com.ribuufing.findlostitem.presentation.home

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
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.domain.use_cases.GetCurrentUserUidUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetUserInfosByUidUseCase
import com.ribuufing.findlostitem.utils.Result
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LostItemsViewModel @Inject constructor(
    private val getLostItemsUseCase: GetLostItemsUseCase,
    private val upVoteItemUseCase: UpVoteItemUseCase,
    private val downVoteItemUseCase: DownVoteItemUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val getUserInfosByUidUseCase: GetUserInfosByUidUseCase,
) : ViewModel() {
    private val _lostItems = MutableStateFlow<List<LostItem>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _userInfos = MutableStateFlow<Result<User?>>(Result.Loading)
    val userInfos: StateFlow<Result<User?>> = _userInfos

    val filteredLostItems: StateFlow<List<LostItem>> =
        combine(_lostItems, _searchQuery) { items, query ->
            val filteredItems = filterLostItems(items, query)
            sortLostItems(filteredItems)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchLostItems()
        getUserInfosByUid()
    }

    private fun sortLostItems(items: List<LostItem>): List<LostItem> {
        return items.sortedByDescending { it.timestamp.seconds }
    }

    private fun fetchLostItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _lostItems.value = getLostItemsUseCase.invoke()
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
                }
            } catch (e: Exception) {
                _userInfos.value = Result.Failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun filterLostItems(items: List<LostItem>, query: String): List<LostItem> {
        return items.filter {
            it.itemName.contains(query, ignoreCase = true) ||
                    it.message.contains(query, ignoreCase = true)
        }
    }

    fun upvoteItem(itemId: String, currentUpvotes: Int) {
        viewModelScope.launch {
            upVoteItemUseCase.invoke(itemId, currentUpvotes)
            refreshLostItems()
        }
    }

    fun downVoteItem(itemId: String, currentDownvotes: Int) {
        viewModelScope.launch {
            downVoteItemUseCase.invoke(itemId, currentDownvotes)
            refreshLostItems()
        }
    }

    fun refreshLostItems() {
        fetchLostItems()
    }
}


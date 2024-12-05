package com.ribuufing.findlostitem.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.domain.use_cases.*
import com.ribuufing.findlostitem.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LostItemsViewModel @Inject constructor(
    private val getLostItemsUseCase: GetLostItemsUseCase,
    private val upVoteItemUseCase: UpVoteItemUseCase,
    private val downVoteItemUseCase: DownVoteItemUseCase,
    private val getUserInfosByUidUseCase: GetUserInfosByUidUseCase,
) : ViewModel() {

    private val _lostItems = MutableStateFlow<List<LostItem>>(emptyList())
    val lostItems: StateFlow<List<LostItem>> = _lostItems.map { items ->
        items.sortedByDescending { calculateVoteRatio(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Cache for sender information
    private val _senderInfoCache = mutableMapOf<String, StateFlow<Result<User?>>>()

    init {
        fetchLostItems()
    }

    fun fetchLostItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _lostItems.value = getLostItemsUseCase()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSenderInfo(senderId: String): StateFlow<Result<User?>> {
        return _senderInfoCache.getOrPut(senderId) {
            flow {
                emit(Result.Loading)
                getUserInfosByUidUseCase(senderId).collect { result ->
                    emit(result)
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = Result.Loading
            )
        }
    }

    fun upvoteItem(itemId: String, currentUpvotes: Int) {
        viewModelScope.launch {
            upVoteItemUseCase(itemId, currentUpvotes)
        }
    }

    fun downVoteItem(itemId: String, currentDownvotes: Int) {
        viewModelScope.launch {
            downVoteItemUseCase(itemId, currentDownvotes)
        }
    }

    fun removeUpvote(itemId: String, currentUpvotes: Int) {
        // Remove upvote from Firebase
        // Similar to upvoteItem but decrements the count
    }

    fun removeDownvote(itemId: String, currentDownvotes: Int) {
        // Remove downvote from Firebase
        // Similar to downVoteItem but increments the count
    }

    private fun calculateVoteRatio(item: LostItem): Double {
        val voteDiff = item.numOfUpVotes - kotlin.math.abs(item.numOfDownVotes)
        return if (voteDiff > 0) voteDiff.toDouble() else 0.0
    }
}


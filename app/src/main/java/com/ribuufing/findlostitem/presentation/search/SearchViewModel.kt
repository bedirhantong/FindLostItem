package com.ribuufing.findlostitem.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.use_cases.GetLostItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getLostItemsUseCase: GetLostItemsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<LostItem>>(emptyList())
    val searchResults: StateFlow<List<LostItem>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        _searchQuery
            .debounce(300)
            .onEach { query ->
                if (query.isNotEmpty()) {
                    searchLostItems(query)
                } else {
                    _searchResults.value = emptyList()
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun searchLostItems(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = getLostItemsUseCase()
                _searchResults.value = items.filter { item ->
                    item.itemName.contains(query, ignoreCase = true) ||
                    item.message.contains(query, ignoreCase = true) ||
                    item.foundWhere.contains(query, ignoreCase = true) ||
                    item.placedWhere.contains(query, ignoreCase = true)
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
} 
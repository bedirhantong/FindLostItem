package com.ribuufing.findlostitem.presentation.mapscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.use_cases.AddDummyDataUseCase
import com.ribuufing.findlostitem.domain.use_cases.GetLostItemsUseCase
import com.ribuufing.findlostitem.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val getLostItemsUseCase: GetLostItemsUseCase,
) : ViewModel() {

    private val _lostItems = MutableStateFlow<List<LostItem>>(emptyList())
    val lostItems: StateFlow<List<LostItem>> = _lostItems

    private val _clusters = MutableStateFlow<List<Cluster>>(emptyList())
    val clusters: StateFlow<List<Cluster>> = _clusters

    private val _selectedClusterItems = MutableStateFlow<List<LostItem>>(emptyList())
    val selectedClusterItems: StateFlow<List<LostItem>> = _selectedClusterItems

    private val _isBottomSheetExpanded = MutableStateFlow(false)
    val isBottomSheetExpanded: StateFlow<Boolean> = _isBottomSheetExpanded
    init {
        fetchLostItems()
//        addDummyData()
    }

    private fun addDummyData() {
        viewModelScope.launch {
            getLostItemsUseCase.addDummyData()
        }
    }
    // Fetch all lost items and cluster them
    private fun fetchLostItems() {
        viewModelScope.launch {
            _lostItems.value = getLostItemsUseCase.invoke()
            clusterLostItems()
        }
    }

    // Clustering logic based on proximity and density
    private fun clusterLostItems() {
        val items = _lostItems.value
        val clusters = mutableListOf<Cluster>()
        val visited = mutableSetOf<LostItem>()

        for (item in items) {
            if (visited.contains(item)) continue

            // Find nearby items within 500 meters
            val clusterItems = items.filter {
                it != item && it.placedWhere.distanceTo(item.placedWhere) < 500
            }
            val cluster = Cluster(
                center = item.placedWhere,
                items = clusterItems + item
            )
            clusters.add(cluster)
            visited.addAll(clusterItems)
            visited.add(item)
        }
        _clusters.value = clusters
    }


    // Handle map click to display cluster items in the bottom sheet
    fun onMapClick(location: Location) {
        viewModelScope.launch {
            val nearbyCluster = _clusters.value.find { cluster ->
                cluster.center.distanceTo(location) < 300 // Adjust radius as needed
            }
            if (nearbyCluster != null) {
                Log.d("MapViewModel", "Cluster found: ${nearbyCluster.items.size} items")
                _selectedClusterItems.value = nearbyCluster.items
                _isBottomSheetExpanded.value = true
            } else {
                Log.d("MapViewModel", "No cluster found near clicked location.")
                _isBottomSheetExpanded.value = false
            }
        }
    }


    fun collapseBottomSheet() {
        _isBottomSheetExpanded.value = false
    }
}

// Data class representing a cluster of lost items
data class Cluster(
    val center: Location,
    val items: List<LostItem>
)

package com.ribuufing.findlostitem.presentation.mapscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.use_cases.GetLostItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow

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

    private val _selectedItem = MutableStateFlow<LostItem?>(null)
    val selectedItem: StateFlow<LostItem?> = _selectedItem

    private val _mapZoom = MutableStateFlow(15f)
    val mapZoom: StateFlow<Float> = _mapZoom

    private val _isMarkerAnimating = MutableStateFlow(false)
    val isMarkerAnimating: StateFlow<Boolean> = _isMarkerAnimating

    init {
        fetchLostItems()
    }

    private fun fetchLostItems() {
        viewModelScope.launch {
            _lostItems.value = getLostItemsUseCase.invoke()
            updateClusters(15f)
        }
    }

    fun updateZoomLevel(zoom: Float) {
        _mapZoom.value = zoom
        updateClusters(zoom)
    }

    fun updateClusters(zoomLevel: Float) {
        val items = _lostItems.value
        val clusters = mutableListOf<Cluster>()
        val visited = mutableSetOf<LostItem>()

        val clusterRadius = calculateClusterRadius(zoomLevel)

        for (item in items) {
            if (visited.contains(item)) continue

            val nearbyItems = items.filter { other ->
                other != item && other.deliverLatLng.distanceTo(item.deliverLatLng) <= clusterRadius
            }

            if (nearbyItems.isEmpty()) {
                clusters.add(Cluster(center = item.deliverLatLng, items = listOf(item)))
                visited.add(item)
            } else {
                val allClusterItems = nearbyItems + item
                val cluster = Cluster(
                    center = calculateClusterCenter(allClusterItems),
                    items = allClusterItems
                )
                clusters.add(cluster)
                visited.addAll(nearbyItems)
                visited.add(item)
            }
        }

        viewModelScope.launch {
            _isMarkerAnimating.value = true
            _clusters.value = clusters
            delay(300)
            _isMarkerAnimating.value = false
        }
    }

    private fun calculateClusterRadius(zoomLevel: Float): Double {
        return when {
            zoomLevel <= 5f -> 10000.0
            zoomLevel <= 10f -> 5000.0
            zoomLevel <= 13f -> 1000.0
            zoomLevel <= 14f -> 400.0
            zoomLevel <= 15f -> 200.0
            zoomLevel <= 16f -> 100.0
            zoomLevel <= 17f -> 75.0
            else -> 50.0
        }
    }

    private fun calculateClusterCenter(items: List<LostItem>): Location {
        val latSum = items.sumOf { it.deliverLatLng.latitude }
        val lngSum = items.sumOf { it.deliverLatLng.longitude }
        return Location(
            latitude = latSum / items.size,
            longitude = lngSum / items.size
        )
    }

    fun onMapClick(location: Location) {
        viewModelScope.launch {
            val clickedMarker = _clusters.value.find { cluster ->
                cluster.center.distanceTo(location) < 20
            }
            
            if (clickedMarker != null) {
                if (clickedMarker.items.size == 1) {
                    _selectedClusterItems.value = clickedMarker.items
                    _selectedItem.value = clickedMarker.items.first()
                } else {
                    _selectedClusterItems.value = clickedMarker.items
                    _selectedItem.value = null
                }
                _isBottomSheetExpanded.value = true
            } else {
                _isBottomSheetExpanded.value = false
                _selectedItem.value = null
                _selectedClusterItems.value = emptyList()
            }
        }
    }

    fun selectItem(item: LostItem) {
        _selectedItem.value = item
    }

    fun collapseBottomSheet() {
        _isBottomSheetExpanded.value = false
        _selectedItem.value = null
    }
}

data class Cluster(
    val center: Location,
    val items: List<LostItem>
)

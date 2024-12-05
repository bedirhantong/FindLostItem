package com.ribuufing.findlostitem.presentation.reportfounditem

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import com.ribuufing.findlostitem.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportFoundItemViewModel @Inject constructor(
    private val lostItemRepository: LostItemRepository
) : ViewModel() {

    private val _itemName = MutableStateFlow("")
    val itemName: StateFlow<String> = _itemName

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _foundWhere = MutableStateFlow("")
    val foundWhere: StateFlow<String> = _foundWhere

    private val _placedWhere = MutableStateFlow("")
    val placedWhere: StateFlow<String> = _placedWhere

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages

    private val _foundLatLng = MutableStateFlow<LatLng?>(null)
    val foundLatLng: StateFlow<LatLng?> = _foundLatLng

    private val _deliverLatLng = MutableStateFlow<LatLng?>(null)
    val deliverLatLng: StateFlow<LatLng?> = _deliverLatLng

    private val _sendStatus = MutableStateFlow<SendStatus>(SendStatus.Idle)
    val sendStatus: StateFlow<SendStatus> = _sendStatus

    private val _formState = MutableStateFlow(FormState())
    val isFormValid = _formState.map { it.isValid() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun updateItemName(value: String) {
        _itemName.value = value
        updateFormState()
    }

    fun updateMessage(value: String) {
        _message.value = value
        updateFormState()
    }

    fun updateFoundWhere(value: String) {
        _foundWhere.value = value
        updateFormState()
    }

    fun updatePlacedWhere(value: String) {
        _placedWhere.value = value
        updateFormState()
    }

    fun updateSelectedImages(images: List<Uri>) {
        _selectedImages.value = images
        updateFormState()
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value = _selectedImages.value.filter { it != uri }
        updateFormState()
    }

    fun updateFoundLocation(latLng: LatLng?) {
        _foundLatLng.value = latLng
        updateFormState()
    }

    fun updateDeliverLocation(latLng: LatLng?) {
        _deliverLatLng.value = latLng
        updateFormState()
    }

    private fun updateFormState() {
        _formState.value = FormState(
            itemName = _itemName.value,
            message = _message.value,
            foundWhere = _foundWhere.value,
            placedWhere = _placedWhere.value,
            hasImages = _selectedImages.value.isNotEmpty(),
            hasFoundLocation = _foundLatLng.value != null,
            hasDeliverLocation = _deliverLatLng.value != null
        )
    }

    fun submitReport() {
        viewModelScope.launch {
            _sendStatus.value = SendStatus.Sending
            
            val result = lostItemRepository.uploadFoundItem(
                itemName = _itemName.value,
                message = _message.value,
                foundWhere = _foundWhere.value,
                placedWhere = _placedWhere.value,
                foundLatLng = _foundLatLng.value!!,
                deliverLatLng = _deliverLatLng.value!!,
                images = _selectedImages.value
            )

            _sendStatus.value = when(result) {
                is Result.Success -> SendStatus.Success
                is Result.Failure -> SendStatus.Error
                else -> SendStatus.Error
            }
        }
    }
}

data class FormState(
    val itemName: String = "",
    val message: String = "",
    val foundWhere: String = "",
    val placedWhere: String = "",
    val hasImages: Boolean = false,
    val hasFoundLocation: Boolean = false,
    val hasDeliverLocation: Boolean = false
) {
    fun isValid(): Boolean {
        return itemName.isNotBlank() &&
                message.isNotBlank() &&
                foundWhere.isNotBlank() &&
                placedWhere.isNotBlank() &&
                hasImages &&
                hasFoundLocation &&
                hasDeliverLocation
    }
} 
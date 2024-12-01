package com.ribuufing.findlostitem.data.model

import kotlinx.serialization.Serializable
import com.google.firebase.Timestamp
import kotlinx.serialization.Contextual

@Serializable
data class LostItem(
    val itemId: String = "",
    val itemName: String = "",
    val message: String = "",
    val images: List<String> = emptyList(),
    @Contextual
    val timestamp: Timestamp = Timestamp(0, 0),
    val senderInfo: SenderInfo = SenderInfo(),
    val foundWhere: String = "",
    val placedWhere: String = "",
    val foundLatLng: Location = Location(0.0, 0.0),
    val deliverLatLng: Location = Location(0.0, 0.0),
    val is_picked: Boolean = false,
    val numOfUpVotes: Int = 0,
    val numOfDownVotes: Int = 0,

    // kullanılmıyor
    val contact: String = "",
    val isFound: Boolean = false,
    )

@Serializable
data class SenderInfo(
    val senderId: String = "",
    val email: String = "",
)

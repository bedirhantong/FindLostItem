package com.ribuufing.findlostitem.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LostItem(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val images: List<String> = emptyList(),
    val date: String = "",
    val contact: String = "",
    val foundByUser: User? = null,
    val isFound: Boolean = false,
    val isReturned: Boolean = false,
    val foundWhere: String = "",
    val placedWhere: String = "",
    val numOfUpVotes: Int = 0,
    val numOfDownVotes: Int = 0
)

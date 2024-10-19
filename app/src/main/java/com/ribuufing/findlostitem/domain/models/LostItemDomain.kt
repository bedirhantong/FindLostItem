package com.ribuufing.findlostitem.domain.models

data class LostItemDomain(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val location: String,
    val contact: String,
    val date: String,
    val status: String,
    val userId: Int
)

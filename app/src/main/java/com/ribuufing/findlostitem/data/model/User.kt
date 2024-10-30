package com.ribuufing.findlostitem.data.model
import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val imageUrl: String = "",
    val phone: String = "",
    val foundedItems: List<LostItem> = emptyList(),
    val chats : List<Chat> = emptyList()
)

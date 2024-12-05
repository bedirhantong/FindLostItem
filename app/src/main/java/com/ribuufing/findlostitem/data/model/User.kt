package com.ribuufing.findlostitem.data.model
import kotlinx.serialization.Serializable


@Serializable
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val imageUrl: String = "",
    val phone: String = "",
    val foundedItems: List<String> = emptyList(),
    val chats : List<String> = emptyList()
)

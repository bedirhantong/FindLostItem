package com.ribuufing.findlostitem.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: String = "0",
    val itemId : String = "0",
    val senderUserUid: String = "",
    val receiverUserUid: String = "",
    val messagesIds: List<String> = emptyList()
)

@Serializable
data class Message(
    val id: String = "",
    val itemId : String = "0",
    val senderUserUid: String = "",
    val receiverUserUid: String = "",
    val content: String = "",
    val date: String = ""
)


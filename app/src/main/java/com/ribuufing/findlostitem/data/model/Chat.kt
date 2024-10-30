package com.ribuufing.findlostitem.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Int = 0,
    val messages: List<Message>
)

@Serializable
data class Message(
    val id: Int = 0,
    val senderUser: User,
    val receiverUser: User,
    val content : String = "",
    val date : String
)

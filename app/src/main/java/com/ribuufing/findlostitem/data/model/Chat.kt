package com.ribuufing.findlostitem.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Chat(
    @DocumentId val id: String = "",
    val itemId: String = "",
    val participants: List<String> = listOf(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Timestamp = Timestamp.now()
)

data class Message(
    @DocumentId val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
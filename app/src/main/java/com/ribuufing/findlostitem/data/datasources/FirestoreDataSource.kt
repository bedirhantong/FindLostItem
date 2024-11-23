package com.ribuufing.findlostitem.data.datasources

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.Message
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FirestoreDataSource(private val firestore: FirebaseFirestore) {

    suspend fun getAllChatsForUser(userUid: String): List<Chat> {
        val querySnapshot = firestore.collection("chats")
            .whereEqualTo("senderUserUid", userUid)
            .get()
            .await()

        val receiverQuerySnapshot = firestore.collection("chats")
            .whereEqualTo("receiverUserUid", userUid)
            .get()
            .await()

        val allChats = mutableListOf<Chat>()
        allChats.addAll(querySnapshot.toObjects(Chat::class.java))
        allChats.addAll(receiverQuerySnapshot.toObjects(Chat::class.java))

        return allChats.distinctBy { it.id }
    }

    suspend fun createChat(senderUid: String, receiverUid: String): Chat {
        val existingChat = getChat(senderUid, receiverUid)
        if (existingChat != null) {
            return existingChat
        }

        val chatRef = firestore.collection("chats").document()
        val chat = Chat(
            id = chatRef.id,
            senderUserUid = senderUid,
            receiverUserUid = receiverUid,
            messagesIds = emptyList()
        )
        chatRef.set(chat).await()
        return chat
    }


    suspend fun getChat(senderUid: String, receiverUid: String): Chat? {
        val querySnapshot = firestore.collection("chats")
            .whereEqualTo("senderUserUid", senderUid)
            .whereEqualTo("receiverUserUid", receiverUid)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()?.toObject(Chat::class.java)
    }


    suspend fun getChatByChatId(chatId: String): Chat? {
        return try {
            val documentSnapshot = firestore.collection("chats")
                .document(chatId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(Chat::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addMessageToChat(chatId: String, message: Message): Boolean {
        val messageRef = firestore.collection("messages").document()
        val newMessage = message.copy(id = messageRef.id)
        messageRef.set(newMessage).await()

        val chatRef = firestore.collection("chats").document(chatId)
        chatRef.update("messagesIds", FieldValue.arrayUnion(newMessage.id)).await()

        return true
    }

    suspend fun getMessagesForChat(chatId: String): List<Message> {
        val chatSnapshot = firestore.collection("chats").document(chatId).get().await()
        val messageIds = chatSnapshot.get("messagesIds") as? List<String> ?: emptyList()

        return if (messageIds.isNotEmpty()) {
            firestore.collection("messages")
                .whereIn(FieldPath.documentId(), messageIds)
                .get()
                .await()
                .toObjects(Message::class.java)
                .sortedBy { it.date }
        } else {
            emptyList()
        }
    }

    suspend fun getAllChatsByUserUid(userUid: String): List<Chat> {
        val querySnapshot = firestore.collection("chats")
            .whereEqualTo("senderUserUid", userUid)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { it.toObject(Chat::class.java) }
    }

    suspend fun fetchLostItems(): List<LostItem> {
        return firestore.collection("lost_items")
            .get()
            .await()
            .toObjects(LostItem::class.java)
    }

    // Tek bir alanı güncelleyen fonksiyon
    suspend fun updateLostItemField(itemId: String, field: String, value: Any) {
        firestore.collection("lost_items")
            .document(itemId)
            .update(field, value)
            .await()
    }

    suspend fun getLostItemById(itemId: String): LostItem {
        return firestore.collection("lost_items")
            .document(itemId)
            .get()
            .await()
            .toObject(LostItem::class.java)!!
    }

    suspend fun getChatById(userUid: String, chatId: Int): Chat? {
        val firestore = FirebaseFirestore.getInstance()

        return try {
            val chatDocument = firestore
                .collection("users")
                .document(userUid)
                .collection("chats")
                .whereEqualTo("id", chatId)
                .get()
                .await()

            if (chatDocument.documents.isNotEmpty()) {
                chatDocument.documents.first().toObject(Chat::class.java)
            } else {
                null // Chat bulunamadı
            }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching chat with ID $chatId for user $userUid: ${e.message}")
            null
        }
    }


    suspend fun getAllChatByUserUid(userUid: String): List<Chat> {
        val firestore = FirebaseFirestore.getInstance()
        val chats = mutableListOf<Chat>()

        try {
            val userChatsSnapshot = firestore
                .collection("users")
                .document(userUid)
                .collection("chats")
                .get()
                .await()

            for (document in userChatsSnapshot.documents) {
                val chat = document.toObject(Chat::class.java)
                if (chat != null) {
                    chats.add(chat)
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching chats for user $userUid: ${e.message}")
        }
        return chats
    }

    suspend fun getLostItemsInArea(location: Location, radius: Double): List<LostItem> {
        val allItems = fetchLostItems()
        return allItems.filter { item ->
            val itemLocation = Location(item.foundWhere.latitude, item.foundWhere.longitude)
            calculateDistance(location, itemLocation) <= radius
        }
    }

    private fun calculateDistance(loc1: Location, loc2: Location): Double {
        val R = 6371 // Earth's radius in kilometers
        val dLat = Math.toRadians(loc2.latitude - loc1.latitude)
        val dLon = Math.toRadians(loc2.longitude - loc1.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(loc1.latitude)) * cos(Math.toRadians(loc2.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

}

package com.ribuufing.findlostitem.data.datasources

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FirestoreDataSource(private val firestore: FirebaseFirestore) {

    fun getChatsForUser(currentUserId: String): Flow<List<Chat>> = callbackFlow {
        val chatQuery = firestore.collection("chats")
            .whereArrayContains("participants", currentUserId)

        val listener = chatQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null&& !snapshot.isEmpty) {
                val chats = snapshot.toObjects(Chat::class.java)
                trySend(chats)
            }else {
                Log.d("FirestoreDataSource", "No chats found.")
            }
        }

        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(chatId: String, message: Message) {
        firestore.collection("chats").document(chatId)
            .collection("messages")
            .add(message)
            .await()

        // Update last message in chat document
        firestore.collection("chats").document(chatId)
            .update(
                mapOf(
                    "lastMessage" to message.content,
                    "lastMessageTimestamp" to message.timestamp
                )
            )
            .await()
    }

    fun getChatMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val query = firestore.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val messages = snapshot.toObjects(Message::class.java)
                trySend(messages)
            }
        }

        awaitClose { listener.remove() }
    }

    fun getOrCreateChat(itemId: String, currentUserId: String, otherUserId: String): Flow<Chat> = callbackFlow {
        val chatQuery = firestore.collection("chats")
            .whereEqualTo("itemId", itemId)
            .whereArrayContains("participants", currentUserId)
            .limit(1)

        val listener = chatQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val chat = snapshot.documents[0].toObject(Chat::class.java)!!
                trySend(chat).isSuccess // Gönderim başarılı mı kontrol et
            } else {
                // Chat yoksa yeni bir tane oluştur
                val newChat = Chat(
                    itemId = itemId,
                    participants = listOf(currentUserId, otherUserId)
                )
                firestore.collection("chats").add(newChat).addOnSuccessListener { documentReference ->
                    val createdChat = newChat.copy(id = documentReference.id)
                    trySend(createdChat).isSuccess // Gönderim başarılı mı kontrol et
                }.addOnFailureListener { e ->
                    close(e) // Hata durumunda akışı kapat
                }
            }
        }

        awaitClose { listener.remove() }
    }

    suspend fun fetchLostItems(): List<LostItem> {
        return firestore.collection("lost_items")
            .get()
            .await()
            .toObjects(LostItem::class.java)
    }

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

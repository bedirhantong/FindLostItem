package com.ribuufing.findlostitem.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.ribuufing.findlostitem.data.model.LostItem
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(private val firestore: FirebaseFirestore) {

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

    // Birden fazla alanı aynı anda güncelleyen fonksiyon
    suspend fun updateMultipleFields(itemId: String, updates: Map<String, Any>) {
        firestore.collection("lost_items")
            .document(itemId)
            .update(updates)
            .await()
    }

    suspend fun addDummyData() {
        val batch = firestore.batch()

        // Dummy user verisi
        val user1 = hashMapOf(
            "id" to 1,
            "name" to "John Doe",
            "email" to "johndoe@example.com",
            "password" to "hashed_password",
            "imageUrl" to "https://pbs.twimg.com/media/Fv8KAF-WYAEjz13.jpg",
            "phone" to "+123456789",
            "foundedItems" to listOf("101", "103")
        )

        val user2 = hashMapOf(
            "id" to 2,
            "name" to "Jane Smith",
            "email" to "janesmith@example.com",
            "password" to "hashed_password",
            "imageUrl" to "https://pbs.twimg.com/media/Fv8KAF-WYAEjz13.jpg",
            "phone" to "+987654321",
            "foundedItems" to listOf("102")
        )

        val user3 = hashMapOf(
            "id" to 3,
            "name" to "Michael Brown",
            "email" to "michaelbrown@example.com",
            "password" to "hashed_password",
            "imageUrl" to "https://pbs.twimg.com/media/Fv8KAF-WYAEjz13.jpg",
            "phone" to "+1122334455",
            "foundedItems" to listOf("104")
        )

        // Dummy lost item verisi
        val lostItem1 = hashMapOf(
            "id" to 101,
            "title" to "Lost Wallet",
            "description" to "Black leather wallet with a zipper",
            "images" to listOf(
                "https://godbolegear.com/cdn/shop/files/Hand_Grained_Leather_Wallet_in_Chestnut_Leather.jpg?v=1717913883&width=823",
                "https://craftandglory.in/cdn/shop/products/DSC07927_1.jpg?v=1660802328&width=1946",
                "https://pbs.twimg.com/media/Fv8KAF-WYAEjz13.jpg",
            ),
            "location" to "Central Park, NYC",
            "date" to "2024-10-10",
            "contact" to "+123456789",
            "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
            "isFound" to false,
            "isReturned" to false,
            "foundWhere" to "Central Park",
            "placedWhere" to "School Security Office",
            "numOfUpVotes" to 5,
            "numOfDownVotes" to 2
        )

        val lostItem2 = hashMapOf(
            "id" to 102,
            "title" to "Lost Keys",
            "description" to "Set of house keys with a red keychain",
            "images" to listOf(
                "https://t3.ftcdn.net/jpg/09/12/74/96/360_F_912749615_ilzdq7BTlvlQvKCfvRjfaOJJnG1Fkyla.jpg",
                "https://images.vivintcdn.com/global/vivint.com/resources/products/smart-lock/lost-keys-keyless-entry.jpg",
                "https://ed77t9fje4v.exactdn.com/wp-content/uploads/2013/03/Lost-House-Keys-Find-a-Locksmith-to-replace-lost-door-and-windows-key.jpg?strip=all&lossy=1&ssl=1"
            ),
            "location" to "Downtown Coffee Shop",
            "date" to "2024-10-12",
            "contact" to "+987654321",
            "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
            "isFound" to false,
            "isReturned" to false,
            "foundWhere" to "Coffee Shop",
            "placedWhere" to "Lost and Found Desk",
            "numOfUpVotes" to 3,
            "numOfDownVotes" to 1
        )

        val lostItem3 = hashMapOf(
            "id" to 103,
            "title" to "Lost Sunglasses",
            "description" to "Ray-Ban aviator sunglasses with gold frames",
            "images" to listOf(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQEhvCW7cZNpTR_oVPbZpiY7MjYFku6CYS61Q&s",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQi9LKR0cHn36sl5QfAvjh0-LUyuPR910zxvUMXbycFYvGurZpH6-8XnJQmwXgBuKcMy3s&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQF9nFeCEJYjIiLGX0CbeOdMMiaMMOT__6uUFlqGup9uNzk8N4Xlgy3-Ne7IL42G46KUjk&usqp=CAU"
            ),
            "location" to "Beach",
            "date" to "2024-10-14",
            "contact" to "+123456789",
            "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
            "isFound" to true,
            "isReturned" to false,
            "foundWhere" to "Beach",
            "placedWhere" to "Beach Security Office",
            "numOfUpVotes" to 7,
            "numOfDownVotes" to 0
        )

        val lostItem4 = hashMapOf(
            "id" to 104,
            "title" to "Lost Laptop",
            "description" to "MacBook Air M1, silver color, in a black case",
            "images" to listOf(
                "https://www.digitaltrends.com/wp-content/uploads/2022/08/macbook-air-m2-5.jpg?fit=720%2C720&p=1",
                "https://shopdunk.com/images/thumbs/0005888_air-m2-silver_1600.jpeg",
                "https://i0.shbdn.com/photos/90/34/89/x5_1146903489oan.jpg",
                "https://media.cnn.com/api/v1/images/stellar/prod/apple-macbook-air-m3-cnnu-lead-options-8.jpg?c=16x9&q=h_833,w_1480,c_fill"
            ),
            "location" to "University Library",
            "date" to "2024-10-16",
            "contact" to "+1122334455",
            "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
            "isFound" to false,
            "isReturned" to false,
            "foundWhere" to "Library",
            "placedWhere" to "University IT Department",
            "numOfUpVotes" to 2,
            "numOfDownVotes" to 4
        )

        // Batch işlemi ile dummy datayı firestore'a ekleme
        val userRef1 = firestore.collection("users").document(user1["id"].toString())
        val userRef2 = firestore.collection("users").document(user2["id"].toString())
        val userRef3 = firestore.collection("users").document(user3["id"].toString())

        batch.set(userRef1, user1)
        batch.set(userRef2, user2)
        batch.set(userRef3, user3)

        // Lost Item belgelerini batch ile ekleme
        val lostItemRef1 = firestore.collection("lost_items").document(lostItem1["id"].toString())
        val lostItemRef2 = firestore.collection("lost_items").document(lostItem2["id"].toString())
        val lostItemRef3 = firestore.collection("lost_items").document(lostItem3["id"].toString())
        val lostItemRef4 = firestore.collection("lost_items").document(lostItem4["id"].toString())

        batch.set(lostItemRef1, lostItem1)
        batch.set(lostItemRef2, lostItem2)
        batch.set(lostItemRef3, lostItem3)
        batch.set(lostItemRef4, lostItem4)

        // Batch işlemini gerçekleştirme
        batch.commit().await()
    }
}

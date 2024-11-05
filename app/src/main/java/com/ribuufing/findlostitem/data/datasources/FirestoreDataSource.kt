package com.ribuufing.findlostitem.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.User
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    suspend fun getLostItemById(itemId: String): LostItem {
        return firestore.collection("lost_items")
            .document(itemId)
            .get()
            .await()
            .toObject(LostItem::class.java)!!
    }

    // Birden fazla alanı aynı anda güncelleyen fonksiyon
    suspend fun updateMultipleFields(itemId: String, updates: Map<String, Any>) {
        firestore.collection("lost_items")
            .document(itemId)
            .update(updates)
            .await()
    }

    suspend fun getLostItemsInArea(location: Location, radius: Double): List<LostItem> {
        val allItems = fetchLostItems()
        return allItems.filter { item ->
            val itemLocation = Location(item.foundWhere.latitude, item.foundWhere.longitude)
            calculateDistance(location, itemLocation) <= radius
        }
    }

    suspend fun addDummyData() {
        val batch = firestore.batch()

        // Dummy user verisi (unchanged)
        val user1 = hashMapOf(
            "id" to 1,
            "name" to "John Doe",
            "email" to "johndoe@example.com",
            "password" to "hashed_password",
            "imageUrl" to "https://pbs.twimg.com/media/Fv8KAF-WYAEjz13.jpg",
            "phone" to "+123456789",
            "foundedItems" to listOf("101", "103", "105", "107", "109")
        )

        val user2 = hashMapOf(
            "id" to 2,
            "name" to "Jane Smith",
            "email" to "janesmith@example.com",
            "password" to "hashed_password",
            "imageUrl" to "https://pbs.twimg.com/media/Fv8KAF-WYAEjz13.jpg",
            "phone" to "+987654321",
            "foundedItems" to listOf("102", "104", "106", "108", "110")
        )

        val user3 = hashMapOf(
            "id" to 3,
            "name" to "Michael Brown",
            "email" to "michaelbrown@example.com",
            "password" to "hashed_password",
            "imageUrl" to "https://pbs.twimg.com/media/Fv8KAF-WYAEjz13.jpg",
            "phone" to "+1122334455",
            "foundedItems" to listOf("111", "113", "115", "117", "119")
        )

        // Expanded list of 20 lost items
        val lostItems = listOf(
            hashMapOf(
                "id" to 101,
                "title" to "Lost Wallet",
                "description" to "Black leather wallet with a zipper",
                "images" to listOf(
                    "https://godbolegear.com/cdn/shop/files/Hand_Grained_Leather_Wallet_in_Chestnut_Leather.jpg?v=1717913883&width=823",
                    "https://craftandglory.in/cdn/shop/products/DSC07927_1.jpg?v=1660802328&width=1946",
                    "https://pbs.twimg.com/media/Fv8KAF-WYAEjz13.jpg",
                ),
                "location" to "Konyaaltı Beach",
                "date" to "2024-10-10",
                "contact" to "+123456789",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8553, 30.6077),
                "placedWhere" to Location(36.8553, 30.6077),
                "numOfUpVotes" to 5,
                "numOfDownVotes" to 2
            ),
            hashMapOf(
                "id" to 102,
                "title" to "Lost Keys",
                "description" to "Set of house keys with a red keychain",
                "images" to listOf(
                    "https://t3.ftcdn.net/jpg/09/12/74/96/360_F_912749615_ilzdq7BTlvlQvKCfvRjfaOJJnG1Fkyla.jpg",
                    "https://images.vivintcdn.com/global/vivint.com/resources/products/smart-lock/lost-keys-keyless-entry.jpg",
                    "https://ed77t9fje4v.exactdn.com/wp-content/uploads/2013/03/Lost-House-Keys-Find-a-Locksmith-to-replace-lost-door-and-windows-key.jpg?strip=all&lossy=1&ssl=1"
                ),
                "location" to "Muratpaşa Park",
                "date" to "2024-10-12",
                "contact" to "+987654321",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8897, 30.7058),
                "placedWhere" to Location(36.8897, 30.7058),
                "numOfUpVotes" to 3,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 103,
                "title" to "Lost Sunglasses",
                "description" to "Ray-Ban aviator sunglasses with gold frames",
                "images" to listOf(
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQEhvCW7cZNpTR_oVPbZpiY7MjYFku6CYS61Q&s",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQi9LKR0cHn36sl5QfAvjh0-LUyuPR910zxvUMXbycFYvGurZpH6-8XnJQmwXgBuKcMy3s&usqp=CAU",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQF9nFeCEJYjIiLGX0CbeOdMMiaMMOT__6uUFlqGup9uNzk8N4Xlgy3-Ne7IL42G46KUjk&usqp=CAU"
                ),
                "location" to "Lara Beach",
                "date" to "2024-10-14",
                "contact" to "+123456789",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to true,
                "isReturned" to false,
                "foundWhere" to Location(36.8520, 30.8415),
                "placedWhere" to Location(36.8520, 30.8415),
                "numOfUpVotes" to 7,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 104,
                "title" to "Lost Laptop",
                "description" to "MacBook Air M1, silver color, in a black case",
                "images" to listOf(
                    "https://www.digitaltrends.com/wp-content/uploads/2022/08/macbook-air-m2-5.jpg?fit=720%2C720&p=1",
                    "https://shopdunk.com/images/thumbs/0005888_air-m2-silver_1600.jpeg",
                    "https://i0.shbdn.com/photos/90/34/89/x5_1146903489oan.jpg",
                    "https://media.cnn.com/api/v1/images/stellar/prod/apple-macbook-air-m3-cnnu-lead-options-8.jpg?c=16x9&q=h_833,w_1480,c_fill"
                ),
                "location" to "Akdeniz University Library",
                "date" to "2024-10-16",
                "contact" to "+1122334455",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8964, 30.6575),
                "placedWhere" to Location(36.8964, 30.6575),
                "numOfUpVotes" to 2,
                "numOfDownVotes" to 4
            ),
            hashMapOf(
                "id" to 105,
                "title" to "iPad Pro",
                "description" to "Space Gray iPad Pro 12.9 inch with Apple Pencil",
                "images" to listOf(
                    "https://duet-cdn.vox-cdn.com/thumbor/0x0:2700x1800/2400x2400/filters:focal(959x1068:960x1069):format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25446250/247111_iPad_Pro_2024_AKrales_1292.jpg",
                    "https://media.cnn.com/api/v1/images/stellar/prod/221028130839-ipad-pro-m2-review-cnnu-1.jpg?c=original"
                ),
                "location" to "Kepez Shopping Mall",
                "date" to "2024-10-17",
                "contact" to "+1122334455",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.9375, 30.7183),
                "placedWhere" to Location(36.9375, 30.7183),
                "numOfUpVotes" to 8,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 106,
                "title" to "AirPods Pro",
                "description" to "White AirPods Pro with charging case",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/22/31/82/x5_116522318296s.jpg",
                    "https://i0.shbdn.com/photos/96/57/97/x5_11329657974mj.jpg",
                    "https://i0.shbdn.com/photos/96/57/97/x5_1132965797kak.jpg"
                ),
                "location" to "Antalya Airport",
                "date" to "2024-10-18",
                "contact" to "+1122334466",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to true,
                "isReturned" to false,
                "foundWhere" to Location(36.9083, 30.8000),
                "placedWhere" to Location(36.9083, 30.8000),
                "numOfUpVotes" to 4,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 107,
                "title" to "Scientific Calculator",
                "description" to "Texas Instruments TI-84 Plus",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/82/23/90/x5_11628223908pt.jpg",
                    "https://i0.shbdn.com/photos/03/38/05/x5_1159033805j7v.jpg"
                ),
                "location" to "Döşemealtı High School",
                "date" to "2024-10-19",
                "contact" to "+1122334477",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(37.0333, 30.6667),
                "placedWhere" to Location(37.0333, 30.6667),
                "numOfUpVotes" to 2,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 108,
                "title" to "Smartphone",
                "description" to "iPhone 13 Pro, Graphite color",
                "images" to listOf(
                    "https://ae01.alicdn.com/kf/S8095f8dfb2584d37b2f455814e2cd14at.jpg_640x640q90.jpg",
                    "https://i0.shbdn.com/photos/24/88/41/x5_1137248841n7x.jpg"
                ),
                "location" to "Kaleici Old Town",
                "date" to "2024-10-20",
                "contact" to "+1122334488",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to true,
                "isReturned" to true,
                "foundWhere" to Location(36.8837, 30.7067),
                "placedWhere" to Location(36.8837, 30.7067),
                "numOfUpVotes" to 6,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 109,
                "title" to "Backpack",
                "description" to "Black North Face backpack with laptop compartment",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/67/02/80/x5_1194670280by3.jpg",
                    "https://i0.shbdn.com/photos/67/02/80/x5_11946702808dk.jpg"
                ),
                "location" to "Antalya Bus Terminal",
                "date" to "2024-10-21",
                "contact" to "+1122334499",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.9119, 30.6872),
                "placedWhere" to Location(36.9119, 30.6872),
                "numOfUpVotes" to 3,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 110,
                "title" to "Camera",
                "description" to "Canon EOS R5 with 24-70mm lens",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/91/23/26/x5_1182912326r76.jpg",
                    "https://i0.shbdn.com/photos/85/64/14/x5_1182856414yj0.jpg"
                ),
                "location" to "Düden Waterfalls",
                "date" to "2024-10-22",
                "contact" to "+1122334400",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to true,
                "isReturned" to false,
                "foundWhere" to Location(36.9119, 30.7086),
                "placedWhere" to Location(36.9119, 30.7086),
                "numOfUpVotes" to 9,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 111,
                "title" to "Smartwatch",
                "description" to "Apple Watch  Series 7, Silver Aluminum Case",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/42/28/28/x5_1187422828r45.jpg",
                    "https://i0.shbdn.com/photos/88/51/68/x5_996885168vas.jpg"
                ),
                "location" to "Antalya Aquarium",
                "date" to "2024-10-23",
                "contact" to "+1122334411",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8589, 30.6375),
                "placedWhere" to Location(36.8589, 30.6375),
                "numOfUpVotes" to 4,
                "numOfDownVotes" to 2
            ),
            hashMapOf(
                "id" to 112,
                "title" to "Passport",
                "description" to "Blue US passport",
                "images" to listOf(
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSzn_FjwM1_WKWsC9gAdcB2G_Ty6cLCraYYkQ&s",
                    "https://www.magdeburger.com.tr/storage/blog/March2022/0Sccjrvz3IZ0solzsGgp.jpg"
                ),
                "location" to "Antalya Museum",
                "date" to "2024-10-24",
                "contact" to "+1122334422",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to true,
                "isReturned" to true,
                "foundWhere" to Location(36.8864, 30.6819),
                "placedWhere" to Location(36.8864, 30.6819),
                "numOfUpVotes" to 10,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 113,
                "title" to "Headphones",
                "description" to "Sony WH-1000XM4 Wireless Noise-Canceling Headphones",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/60/35/80/x5_11626035802ci.jpg",
                    "https://i0.shbdn.com/photos/60/35/80/x5_11626035802ci.jpg"
                ),
                "location" to "Antalya City Theater",
                "date" to "2024-10-25",
                "contact" to "+1122334433",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8869, 30.7042),
                "placedWhere" to Location(36.8869, 30.7042),
                "numOfUpVotes" to 5,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 114,
                "title" to "Umbrella",
                "description" to "Black automatic folding umbrella",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/90/51/10/x5_1194905110v80.jpg",
                    "https://i0.shbdn.com/photos/95/78/47/x5_1197957847mgd.jpg"
                ),
                "location" to "Antalya Clock Tower",
                "date" to "2024-10-26",
                "contact" to "+1122334444",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to true,
                "isReturned" to false,
                "foundWhere" to Location(36.8836, 30.7056),
                "placedWhere" to Location(36.8836, 30.7056),
                "numOfUpVotes" to 2,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 115,
                "title" to "Tablet",
                "description" to "Samsung Galaxy Tab S7, Mystic Black",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/30/18/81/x5_1207301881piu.jpg",
                    "https://i0.shbdn.com/photos/30/18/81/x5_1207301881piu.jpg"
                ),
                "location" to "Antalya State Hospital",
                "date" to "2024-10-27",
                "contact" to "+1122334455",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8919, 30.6814),
                "placedWhere" to Location(36.8919, 30.6814),
                "numOfUpVotes" to 6,
                "numOfDownVotes" to 2
            ),
            hashMapOf(
                "id" to 116,
                "title" to "Glasses",
                "description" to "Tortoiseshell frame reading glasses",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/12/27/82/x5_11131227822a3.jpg",
                    "https://i0.shbdn.com/photos/88/10/79/x5_11408810794at.jpg"
                ),
                "location" to "Antalya Cultural Center",
                "date" to "2024-10-28",
                "contact" to "+1122334466",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to true,
                "isReturned" to true,
                "foundWhere" to Location(36.8858, 30.7075),
                "placedWhere" to Location(36.8858, 30.7075),
                "numOfUpVotes" to 3,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 117,
                "title" to "Bicycle",
                "description" to "Red mountain bike, Trek brand",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/84/13/15/x5_1161841315s6o.jpg",
                    "https://i0.shbdn.com/photos/84/13/15/x5_1161841315ddh.jpg"
                ),
                "location" to "Konyaaltı Beach Park",
                "date" to "2024-10-29",
                "contact" to "+1122334477",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8736, 30.6464),
                "placedWhere" to Location(36.8736, 30.6464),
                "numOfUpVotes" to 8,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 118,
                "title" to "Jewelry",
                "description" to "Gold necklace with heart pendant",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/19/39/18/x5_1161193918n1f.jpg",
                ),
                "location" to "Antalya Grand Bazaar",
                "date" to "2024-10-30",
                "contact" to "+1122334488",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to true,
                "isReturned" to false,
                "foundWhere" to Location(36.8839, 30.7064),
                "placedWhere" to Location(36.8839, 30.7064),
                "numOfUpVotes" to 12,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 119,
                "title" to "Drone",
                "description" to "DJI Mavic Air 2 with controller",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/04/79/63/x5_1129047963b4f.jpg",
                    "https://i0.shbdn.com/photos/39/90/66/x5_1142399066yfc.jpg"
                ),
                "location" to "Termessos Ancient City",
                "date" to "2024-10-31",
                "contact" to "+1122334499",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(37.0069, 30.4631),
                "placedWhere" to Location(37.0069, 30.4631),
                "numOfUpVotes" to 7,
                "numOfDownVotes" to 3
            ),
            hashMapOf(
                "id" to 120,
                "title" to "Hiking Boots",
                "description" to "Merrell Moab 2 Waterproof Hiking Boots, size 42",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/59/63/35/x5_1150596335a7d.jpg",
                ),
                "location" to "Olympos Ancient City",
                "date" to "2024-11-01",
                "contact" to "+1122334400",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to true,
                "isReturned" to true,
                "foundWhere" to Location(36.5275, 30.4747),
                "placedWhere" to Location(36.5275, 30.4747),
                "numOfUpVotes" to 5,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 121,
                "title" to "Woman Bag",
                "description" to "Woman bag with a floral pattern",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/13/83/34/x5_1174138334681.jpg",
                    "https://cdn.dsmcdn.com/mnresize/400/-/ty1291/product/media/images/prod/SPM/PIM/20240503/10/49ab5b61-88ec-3b4b-a475-25d294a01079/1_org.jpg"
                ),
                "location" to "Antalya Kaleiçi Marina",
                "date" to "2024-11-02",
                "contact" to "+1122334411",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8840, 30.7056),
                "placedWhere" to Location(36.8840, 30.7056),
                "numOfUpVotes" to 3,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 122,
                "title" to "Phone Case",
                "description" to "Xiaomi Case for Xiaomi 13 Pro, Midnight",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/54/39/43/x5_1185543943wl1.jpg",
                    "https://i0.shbdn.com/photos/26/61/14/x5_12002661144b0.jpg"
                ),
                "location" to "Antalya Aquarium",
                "date" to "2024-11-03",
                "contact" to "+1122334422",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to true,
                "isReturned" to true,
                "foundWhere" to Location(36.8589, 30.6375),
                "placedWhere" to Location(36.8589, 30.6375),
                "numOfUpVotes" to 6,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 123,
                "title" to "Wedding Ring",
                "description" to "Platinum wedding ring with diamonds",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/36/24/13/x5_11453624136ps.jpg",
                ),
                "location" to "Antalya Museum",
                "date" to "2024-11-04",
                "contact" to "+1122334433",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8864, 30.6819),
                "placedWhere" to Location(36.8864, 30.6819),
                "numOfUpVotes" to 5,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 124,
                "title" to "Mouse",
                "description" to "Logitech MX Master 3 Wireless Mouse",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/90/73/80/x5_11499073800k7.jpg",
                ),
                "location" to "Antalya Clock Tower",
                "date" to "2024-11-05",
                "contact" to "+1122334444",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to true,
                "isReturned" to false,
                "foundWhere" to Location(36.8869, 30.7042),
                "placedWhere" to Location(36.8869, 30.7042),
                "numOfUpVotes" to 2,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 125,
                "title" to "Bluetooth Speaker",
                "description" to "JBL Charge 5 Portable Bluetooth Speaker",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/10/64/86/x5_1146106486zbz.jpg",
                    "https://i0.shbdn.com/photos/90/34/89/x5_1146903489oan.jpg"
                ),
                "location" to "Antalya State Hospital",
                "date" to "2024-11-06",
                "contact" to "+1122334455",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8919, 30.6814),
                "placedWhere" to Location(36.8919, 30.6814),
                "numOfUpVotes" to 8,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 126,
                "title" to "100$ Cash",
                "description" to "100 US dollars in a blue envelope",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/74/44/96/x5_1125744496cvw.jpg",
                ),
                "location" to "Antalya Grand Bazaar",
                "date" to "2024-11-07",
                "contact" to "+1122334466",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to true,
                "isReturned" to false,
                "foundWhere" to Location(36.8836, 30.7056),
                "placedWhere" to Location(36.8836, 30.7056),
                "numOfUpVotes" to 4,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 127,
                "title" to "GS Shirt",
                "description" to "Galatasaray SK football shirt, size M",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/54/11/62/x5_1129541162fst.jpg",
                ),
                "location" to "Antalya Kaleiçi Marina",
                "date" to "2024-11-08",
                "contact" to "+1122334477",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8839, 30.7064),
                "placedWhere" to Location(36.8839, 30.7064),
                "numOfUpVotes" to 5,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 128,
                "title" to "BJK Shirt",
                "description" to "Beşiktaş JK football shirt, size L",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/97/15/52/1161971552box.jpg",
                ),
                "location" to "Antalya Museum",
                "date" to "2024-11-09",
                "contact" to "+1122334488",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to true,
                "isReturned" to true,
                "foundWhere" to Location(36.8864, 30.6819),
                "placedWhere" to Location(36.8864, 30.6819),
                "numOfUpVotes" to 6,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 129,
                "title" to "Fenerbahçe Shirt",
                "description" to "Fenerbahçe SK football shirt, size XL",
                "images" to listOf(
                    "https://i0.shbdn.com/photos/49/12/91/x5_1135491291lcr.jpg",
                ),
                "location" to "Antalya Clock Tower",
                "date" to "2024-11-10",
                "contact" to "+1122334499",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8869, 30.7042),
                "placedWhere" to Location(36.8869, 30.7042),
                "numOfUpVotes" to 8,
                "numOfDownVotes" to 1
            ),
            hashMapOf(
                "id" to 130,
                "title" to "Credit Card",
                "description" to "Visa credit card in a black wallet",
                "images" to listOf(
                    "https://uludaggazete.com/wp-content/uploads/2022/10/maxresdefault-1-1024x576.jpg",
                    "https://i.ytimg.com/vi/qTvbQ7YkGjk/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLBmWYuL12lAquSCY9oUhhxUBIr0cg"
                ),
                "location" to "Antalya State Hospital",
                "date" to "2024-11-11",
                "contact" to "+1122334400",
                "foundByUser" to hashMapOf("id" to 3, "name" to "Michael Brown"),
                "isFound" to true,
                "isReturned" to false,
                "foundWhere" to Location(36.8919, 30.6814),
                "placedWhere" to Location(36.8919, 30.6814),
                "numOfUpVotes" to 4,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 131,
                "title" to "University Idendity Card",
                "description" to "University of Antalya student ID card",
                "images" to listOf(
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8psTtDBwSe_fkkHWPWeyx6KdXasml_t4nUw&s",
                    "https://pbs.twimg.com/media/FBvgA1UWQAQKwY6.jpg:large"
                ),
                "location" to "Antalya Grand Bazaar",
                "date" to "2024-11-12",
                "contact" to "+1122334411",
                "foundByUser" to hashMapOf("id" to 1, "name" to "John Doe"),
                "isFound" to false,
                "isReturned" to false,
                "foundWhere" to Location(36.8837, 30.7067),
                "placedWhere" to Location(36.8837, 30.7067),
                "numOfUpVotes" to 2,
                "numOfDownVotes" to 0
            ),
            hashMapOf(
                "id" to 132,
                "title" to "Bank Card",
                "description" to "Ziraat Bank debit card",
                "images" to listOf(
                    "https://www.ziraatbank.com.tr/PublishingImages/Subpage/bireysel/kartlar/card-front.png",
                ),
                "location" to "Antalya Aquarium",
                "date" to "2024-11-13",
                "contact" to "+1122334422",
                "foundByUser" to hashMapOf("id" to 2, "name" to "Jane Smith"),
                "isFound" to true,
                "isReturned" to false,
                "foundWhere" to Location(36.8589, 30.6375),
                "placedWhere" to Location(36.8589, 30.6375),
                "numOfUpVotes" to 5,
                "numOfDownVotes" to 1
            ),

        )

        // Batch işlemi ile dummy datayı firestore'a ekleme
        val userRef1 = firestore.collection("users").document(user1["id"].toString())
        val userRef2 = firestore.collection("users").document(user2["id"].toString())
        val userRef3 = firestore.collection("users").document(user3["id"].toString())

        batch.set(userRef1, user1)
        batch.set(userRef2, user2)
        batch.set(userRef3, user3)

        // Lost Item belgelerini batch ile ekleme
        lostItems.forEach { item ->
            val lostItemRef = firestore.collection("lost_items").document(item["id"].toString())
            batch.set(lostItemRef, item)
        }

        // Batch işlemini gerçekleştirme
        batch.commit().await()
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

<div align="center">
  <img src="arts/find_lost_items.png" alt="App Logo" width="1200"/>
  <h1>Lost & Found Items University App</h1>
  <p>
    <strong>Find Lost Items aims to help students and staff at the university easily report, find, and communicate about lost and found items.
</strong>
  </p>
</div>


## ✨ Features

<details>
<summary><b>🎯 Core Features</b></summary>

* **Lost Item Management**
  - Report found items with multiple images
  - Detailed item descriptions
  - Precise location marking
  - Real-time status updates

* **Location Features**
  - Interactive campus map
  - Location clustering
  - Custom map styling
  - Location selection for found items
</details>

<details>
<summary><b>💬 Communication</b></summary>

* **Messaging System**
  - Direct messaging between users
  - Real-time chat updates
  - Message history
  - User status indicators

* **Notifications**
  - New message alerts
  - Item status updates
  - Location-based notifications
</details>


## Screenshots


| splash | login | signup |
|:-:|:-:|:-:|
| <img src="arts/splash.png" alt="drawing" width="250"/> | <img src="arts/login.png" alt="drawing" width="250"/> | <img src="arts/signup.png" alt="drawing" width="250"/> |
| home | search | post |
| <img src="arts/home.png" alt="drawing" width="250"/> | <img src="arts/search.png" alt="drawing" width="250"/> | <img src="arts/new_post.png" alt="drawing" width="250"/> 
| map | profile | movie detail |
| <img src="arts/map.png" alt="drawing" width="250"/> | <img src="arts/profile.png" alt="drawing" width="250"/> | <img src="arts/item_detail.png" alt="drawing" width="250"/> 



## 🛠️ Technology Stack

<details>
<summary><b>📱 Frontend</b></summary>

* **UI Framework**
  - Jetpack Compose
  - Material 3 Design
  - Custom Composables
  - Navigation Component

* **State Management**
  - ViewModel
  - Kotlin Flow
  - StateFlow
</details>

<details>
<summary><b>🔧 Backend & Data</b></summary>

* **Firebase Services**
  - Authentication
  - Firestore
  - Storage
  - Analytics

* **Local Storage**
  - SharedPreferences
  - Room Database (planned)
</details>

<details>
<summary><b>📚 Libraries</b></summary>

* **Dependency Injection**
  - Dagger Hilt

* **Image Loading**
  - Coil

* **Maps**
  - Google Maps SDK
  - Maps Compose

* **Other**
  - Kotlin Coroutines
  - Android KTX
</details>

## 🔒 Security

<details>
<summary><b>ProGuard Rules</b></summary>

* **Model Classes Protection**
  - Keep data models
  - Preserve Firebase classes
  - Protect Compose components

* **Library Configurations**
  - Hilt ProGuard rules
  - Navigation component rules
  - Model class preservation
</details>

## Firebase Configuration

To run this project, you need to set up Firebase in your development environment:

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app to your Firebase project
3. Download the `google-services.json` file
4. Place the `google-services.json` file in the `app/` directory
5. Copy `google-services.example.json` to `google-services.json` and update with your Firebase credentials

**Important:** Never commit your actual `google-services.json` file to version control. It contains sensitive information that should be kept private.

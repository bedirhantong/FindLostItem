package com.ribuufing.findlostitem.presentation.auth.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.presentation.auth.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.ribuufing.findlostitem.utils.Result

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : UserRepository {

    override fun registerUser(email: String, password: String, name: String): Flow<Result<FirebaseUser?>> = flow {
        emit(Result.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            //add user to firestore database with all the details
            val hashMap = hashMapOf(
                "uid" to user?.uid, // Use Firebase UID
                "name" to name,
                "password" to password,
                "email" to email,
                "imageUrl" to "",
                "phone" to "",
                "foundedItems" to emptyList<LostItem>(),
                "chats" to emptyList<Chat>()
            )
            //add user to firestore database with all the details
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(user?.uid!!).set(hashMap).await()



            emit(Result.Success(result.user)) // Başarı durumunu emit ediyoruz.
        } catch (e: Exception) {
            emit(Result.Failure(e)) // Hata durumunu emit ediyoruz.
        }
    }

    override fun loginUser(email: String, password: String): Flow<Result<FirebaseUser?>> = flow {
        emit(Result.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            emit(Result.Success(result.user))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun logoutUser(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val result = auth.signOut()
            emit(Result.Success(result))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun fetchCurrentUserUid(): String {
        Log.d("UserRepositoryImpl", "fetchCurrentUserUid: ${auth.currentUser?.uid}")
        return auth.currentUser?.uid ?: ""
    }

    override fun fetchUserInfosByUid (uid: String): Flow<Result<User?>> = flow {
        emit(Result.Loading)
        try {
            val db = FirebaseFirestore.getInstance()
            val document = db.collection("users").document(uid).get().await()
            val user = document.toObject(User::class.java)
            Log.d("UserRepositoryImpl", "fetchUserInfosByUid: $user")

            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}
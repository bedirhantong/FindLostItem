package com.ribuufing.findlostitem.presentation.auth.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ribuufing.findlostitem.presentation.auth.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.ribuufing.findlostitem.utils.Result

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : UserRepository {

    override fun registerUser(email: String, password: String): Flow<Result<FirebaseUser?>> = flow {
        emit(Result.Loading) // Burada Loading durumunu emit ediyoruz.
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
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

//    override fun deleteUser(): Flow<Result<Unit>> = flow {
//        try {
//            emit(Result.Loading)
//            val user = auth.currentUser
//            if (user != null) {
//                val result = auth.currentUser?.delete()?.await()
//                emit(Result.Success(result))
//            } else {
//                emit(Result.Failure(Exception("User not logged in")))
//            }
//        } catch (e: Exception) {
//            emit(Result.Failure(e))
//        }
//    }
}
package com.ribuufing.findlostitem.presentation.auth.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.utils.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun registerUser(email: String, password: String, name: String) : Flow<Result<FirebaseUser?>>
    fun loginUser(email: String, password: String): Flow<Result<FirebaseUser?>>
    fun logoutUser(): Flow<Result<Unit>>
    fun fetchCurrentUserUid(): String
    fun fetchUserInfosByUid(uid: String): Flow<Result<User?>>
//    fun deleteUser(): Flow<Result<Unit>>
}
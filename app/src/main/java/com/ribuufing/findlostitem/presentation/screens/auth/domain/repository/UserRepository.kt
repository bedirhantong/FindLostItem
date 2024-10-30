package com.ribuufing.findlostitem.presentation.screens.auth.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.ribuufing.findlostitem.utils.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun registerUser(email: String, password: String) : Flow<Result<FirebaseUser?>>
    fun loginUser(email: String, password: String): Flow<Result<FirebaseUser?>>
    fun logoutUser(): Flow<Result<Unit>>
//    fun deleteUser(): Flow<Result<Unit>>
}
package com.ribuufing.findlostitem.presentation.auth.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.ribuufing.findlostitem.presentation.auth.domain.repository.UserRepository
import com.ribuufing.findlostitem.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(email: String, password: String): Flow<Result<FirebaseUser?>> {
        return userRepository.registerUser(email, password)
    }
}
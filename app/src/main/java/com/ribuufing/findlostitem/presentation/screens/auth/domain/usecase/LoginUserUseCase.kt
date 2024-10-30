package com.ribuufing.findlostitem.presentation.screens.auth.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.ribuufing.findlostitem.presentation.screens.auth.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.ribuufing.findlostitem.utils.Result

class LoginUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(email: String, password: String): Flow<Result<FirebaseUser?>> {
        return userRepository.loginUser(email, password)
    }
}
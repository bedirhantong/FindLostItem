package com.ribuufing.findlostitem.presentation.screens.profile.domain.usecase

import com.ribuufing.findlostitem.presentation.screens.auth.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.ribuufing.findlostitem.utils.Result

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Result<Unit>> = userRepository.logoutUser()
}
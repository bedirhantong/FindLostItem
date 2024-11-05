package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.presentation.auth.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserUidUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): String {
        return userRepository.fetchCurrentUserUid()
    }
}

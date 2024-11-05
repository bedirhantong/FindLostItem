package com.ribuufing.findlostitem.domain.use_cases

import com.google.firebase.auth.FirebaseUser
import com.ribuufing.findlostitem.presentation.auth.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.ribuufing.findlostitem.utils.Result


class GetUserInfosByUidUseCase@Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(uid: String)  = userRepository.fetchUserInfosByUid(uid)
}
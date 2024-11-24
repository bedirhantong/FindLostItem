package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.ribuufing.findlostitem.utils.Result

class GetAllChatsForUserUseCase @Inject constructor(
    private val lostItemRepository: LostItemRepository
) {
    suspend operator fun invoke(userUid: String): Flow<Result<List<Chat>>> = flow {
        emit(Result.Loading)
        try {
            val chats = lostItemRepository.getAllChatsForUser(userUid)
            emit(Result.Success(chats))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}


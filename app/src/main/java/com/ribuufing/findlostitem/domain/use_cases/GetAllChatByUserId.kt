package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import javax.inject.Inject

class GetAllChatByUserId  @Inject constructor(
    private val repository: LostItemRepository
) {
    suspend operator fun invoke(userUid : String): List<Chat> {
        return repository.getAllChatByUserUid(userUid)
    }
}
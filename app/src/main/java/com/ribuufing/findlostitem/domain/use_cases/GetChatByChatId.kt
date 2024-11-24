package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import com.ribuufing.findlostitem.utils.Result
import javax.inject.Inject

class GetChatByChatId @Inject constructor(
    private val repository: LostItemRepository
) {
    suspend fun getChatByChatId(chatId: String): Result<Chat?> {
        return repository.getChatByChatId(chatId)
    }
}
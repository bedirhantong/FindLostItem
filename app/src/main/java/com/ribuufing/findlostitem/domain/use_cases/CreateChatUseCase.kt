package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.data.model.Chat
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.ribuufing.findlostitem.utils.Result
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val chatRepository: LostItemRepository
) {

}
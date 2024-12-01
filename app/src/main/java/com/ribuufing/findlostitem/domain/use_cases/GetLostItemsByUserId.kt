package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetLostItemsByUserId @Inject constructor(
    private val repository: LostItemRepository
) {
    suspend operator fun invoke(userId: String) = flow {
        emit(repository.getLostItemsByUserId(userId))
    }
}
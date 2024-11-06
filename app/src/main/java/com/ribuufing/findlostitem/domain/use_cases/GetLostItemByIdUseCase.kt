package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetLostItemByIdUseCase@Inject constructor(
    private val repository: LostItemRepository
) {
    suspend operator fun invoke(itemId: String) = flow {
        emit(repository.getLostItemById(itemId))
    }
}
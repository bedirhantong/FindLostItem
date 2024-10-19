package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import javax.inject.Inject

class DownVoteItemUseCase @Inject constructor(
    private val repository: LostItemRepository
) {
    suspend operator fun invoke(itemId: String, currentDownvotes: Int) {
        repository.downvoteLostItem(itemId, currentDownvotes)
    }
}
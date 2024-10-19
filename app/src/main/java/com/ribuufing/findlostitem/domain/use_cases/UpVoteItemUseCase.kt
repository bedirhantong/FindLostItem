package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import javax.inject.Inject

class UpVoteItemUseCase @Inject constructor(
    private val repository: LostItemRepository
) {
    suspend operator fun invoke(itemId: String, currentUpvotes: Int) {
        repository.upvoteLostItem(itemId, currentUpvotes)
    }
}
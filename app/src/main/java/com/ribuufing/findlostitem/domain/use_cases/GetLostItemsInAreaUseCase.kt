package com.ribuufing.findlostitem.domain.use_cases

import com.ribuufing.findlostitem.data.model.Location
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import javax.inject.Inject

class GetLostItemsInAreaUseCase @Inject constructor(
    private val repository: LostItemRepository
) {
    suspend operator fun invoke(location: Location, radius: Double): List<LostItem>{
        return repository.getLostItemsInArea(location, radius)
    }
}
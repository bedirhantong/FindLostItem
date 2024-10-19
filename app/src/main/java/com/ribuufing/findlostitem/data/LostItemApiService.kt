package com.ribuufing.findlostitem.data

import com.ribuufing.findlostitem.data.model.LostItem
import retrofit2.http.GET

interface LostItemApiService {
    @GET("lostitems") // API endpoint
    suspend fun getLostItemsFromApi(): List<LostItem>
}
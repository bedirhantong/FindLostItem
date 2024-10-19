package com.ribuufing.findlostitem.data

import com.ribuufing.findlostitem.data.model.LostItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("lost_items") // Kayıp eşyaları almak için endpoint
    suspend fun getLostItems(): List<LostItem>

    @POST("lost_items") // Kayıp eşya eklemek için endpoint
    suspend fun addLostItem(@Body item: LostItem)
}
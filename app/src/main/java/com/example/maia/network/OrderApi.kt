package com.example.maia.network

import com.example.maia.model.order.Order
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderApi {
    @GET("api/Orders")
    suspend fun getOrders(): List<Order>

    @POST("api/Orders")
    suspend fun placeOrder(): Order
}

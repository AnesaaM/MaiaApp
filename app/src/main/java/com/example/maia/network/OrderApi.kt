package com.example.maia.network

import com.example.maia.model.order.Order
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderApi {
    @GET("gateway/women/Order")
    suspend fun getOrders(): List<Order>

    @POST("gateway/women/Order")
    suspend fun placeOrder(): Order
}

package com.example.maia.network

import com.example.maia.model.MenCard
import com.example.maia.model.cart.AddToCartRequest
import com.example.maia.model.cart.CartItem
import com.example.maia.model.order.Order
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MenApi {
    @GET("gateway/men/MenCards")
    suspend fun getMenCards(): List<MenCard>

    @GET("gateway/men/MenCards/search")
    suspend fun searchMenCards(@Query("query") query: String): List<MenCard>

    @GET("gateway/men/Cart")
    suspend fun getCart(): List<CartItem>

    @POST("gateway/men/Cart")
    suspend fun addToCart(@Body request: AddToCartRequest): CartItem

    @DELETE("gateway/men/Cart/{id}")
    suspend fun removeFromCart(@Path("id") id: Int)

    @GET("gateway/men/Order")
    suspend fun getOrders(): List<Order>

    @POST("gateway/men/Order")
    suspend fun placeOrder(): Order
}

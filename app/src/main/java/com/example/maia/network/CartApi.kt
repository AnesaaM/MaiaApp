package com.example.maia.network

import com.example.maia.model.cart.AddToCartRequest
import com.example.maia.model.cart.CartItem
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CartApi {
    @GET("gateway/women/Cart")
    suspend fun getCart(): List<CartItem>

    @POST("gateway/women/Cart")
    suspend fun addToCart(@Body request: AddToCartRequest): CartItem

    @DELETE("gateway/women/Cart/{id}")
    suspend fun removeFromCart(@Path("id") id: Int)
}

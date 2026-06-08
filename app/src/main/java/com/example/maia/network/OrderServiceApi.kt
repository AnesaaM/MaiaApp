package com.example.maia.network

import com.example.maia.model.cart.AddToCartRequest
import com.example.maia.model.cart.CartItem
import com.example.maia.model.cart.CartResponse
import com.example.maia.model.order.Order
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderServiceApi {
    // Cart
    @GET("gateway/orders/Cart")
    suspend fun getCart(): CartResponse

    @POST("gateway/orders/Cart")
    suspend fun addToCart(@Body request: AddToCartRequest)

    @DELETE("gateway/orders/Cart/{id}")
    suspend fun removeFromCart(@Path("id") id: Int)

    @DELETE("gateway/orders/Cart/clear")
    suspend fun clearCart()

    // Orders
    @POST("gateway/orders/Order")
    suspend fun placeOrder(@Body body: JsonObject): Order

    @GET("gateway/orders/Order")
    suspend fun getMyOrders(): List<Order>

    @PATCH("gateway/orders/Order/{id}/status")
    suspend fun updateOrderStatus(@Path("id") id: Int, @Body status: Map<String, String>)

    @GET("gateway/orders/Order/all")
    suspend fun getAllOrders(): List<Order>
}

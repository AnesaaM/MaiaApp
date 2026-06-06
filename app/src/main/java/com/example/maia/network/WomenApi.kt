package com.example.maia.network

import com.example.maia.model.WomenCard
import com.example.maia.model.cart.AddToCartRequest
import com.example.maia.model.cart.CartItem
import com.example.maia.model.order.Order
import com.example.maia.model.wishlist.AddToWishlistRequest
import com.example.maia.model.wishlist.WishlistItem
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WomenApi {
    @GET("gateway/women/CardsWomen/women")
    suspend fun getWomenCards(): List<WomenCard>

    @GET("gateway/women/CardsWomen/browse")
    suspend fun browseWomenCards(): List<WomenCard>

    @GET("gateway/women/Cart")
    suspend fun getCart(): List<CartItem>

    @POST("gateway/women/Cart")
    suspend fun addToCart(@Body request: AddToCartRequest): CartItem

    @DELETE("gateway/women/Cart/{id}")
    suspend fun removeFromCart(@Path("id") id: Int)

    @GET("gateway/women/Wishlist")
    suspend fun getWishlist(): List<WishlistItem>

    @POST("gateway/women/Wishlist")
    suspend fun addToWishlist(@Body request: AddToWishlistRequest): WishlistItem

    @DELETE("gateway/women/Wishlist/{id}")
    suspend fun removeFromWishlist(@Path("id") id: Int)

    @GET("gateway/women/Order")
    suspend fun getOrders(): List<Order>

    @POST("gateway/women/Order")
    suspend fun placeOrder(): Order
}

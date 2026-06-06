package com.example.maia.network

import com.example.maia.model.wishlist.AddToWishlistRequest
import com.example.maia.model.wishlist.WishlistItem
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WishlistApi {
    @GET("gateway/women/Wishlist")
    suspend fun getWishlist(): List<WishlistItem>

    @POST("gateway/women/Wishlist")
    suspend fun addToWishlist(@Body request: AddToWishlistRequest): WishlistItem

    @DELETE("gateway/women/Wishlist/{id}")
    suspend fun removeFromWishlist(@Path("id") id: Int)
}

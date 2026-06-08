package com.example.maia.network

import com.example.maia.model.KidsCards
import com.example.maia.model.cart.AddToCartRequest
import com.example.maia.model.cart.CartItem
import com.example.maia.model.kids.KidsCategory
import com.example.maia.model.kids.KidsProductType
import com.example.maia.model.order.Order
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface KidsApi {
    @GET("gateway/kids/KidsCards")
    suspend fun getKidsCards(): List<KidsCards>

    @GET("gateway/kids/KidsCards/search")
    suspend fun searchKidsCards(@Query("query") query: String): List<KidsCards>

    @GET("gateway/kids/Cart")
    suspend fun getCart(): List<CartItem>

    @POST("gateway/kids/Cart")
    suspend fun addToCart(@Body request: AddToCartRequest): CartItem

    @DELETE("gateway/kids/Cart/{id}")
    suspend fun removeFromCart(@Path("id") id: Int)

    @GET("gateway/kids/Order")
    suspend fun getOrders(): List<Order>

    @POST("gateway/kids/Order")
    suspend fun placeOrder(): Order

    @PATCH("gateway/kids/KidsCards/{id}/sale")
    suspend fun setKidsDiscount(@Path("id") id: Int, @Body body: Map<String, Int>)

    @POST("gateway/kids/KidsCards")
    suspend fun createKidsCard(@Body body: Map<String, Any>): KidsCards

    @PUT("gateway/kids/KidsCards/{id}")
    suspend fun updateKidsCard(@Path("id") id: Int, @Body body: Map<String, Any>): KidsCards

    @DELETE("gateway/kids/KidsCards/{id}")
    suspend fun deleteKidsCard(@Path("id") id: Int)

    @GET("gateway/kids/KidsCategory")
    suspend fun getCategories(): List<KidsCategory>

    @POST("gateway/kids/KidsCategory")
    suspend fun createCategory(@Body body: Map<String, String>): KidsCategory

    @PUT("gateway/kids/KidsCategory/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body body: Map<String, String>): KidsCategory

    @DELETE("gateway/kids/KidsCategory/{id}")
    suspend fun deleteCategory(@Path("id") id: Int)

    @GET("gateway/kids/KidsProductType")
    suspend fun getProductTypes(): List<KidsProductType>

    @POST("gateway/kids/KidsProductType")
    suspend fun createProductType(@Body body: Map<String, String>): KidsProductType

    @PUT("gateway/kids/KidsProductType/{id}")
    suspend fun updateProductType(@Path("id") id: Int, @Body body: Map<String, String>): KidsProductType

    @DELETE("gateway/kids/KidsProductType/{id}")
    suspend fun deleteProductType(@Path("id") id: Int)
}

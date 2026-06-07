package com.example.maia.network

import com.example.maia.model.WomenCard
import com.example.maia.model.women.SetDiscountRequest
import com.example.maia.model.women.WomenCardRequest
import com.example.maia.model.women.WomenCategory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WomenManagerApi {
    @GET("gateway/women/CardsWomen")
    suspend fun getAllCards(): List<WomenCard>

    @POST("gateway/women/CardsWomen")
    suspend fun createCard(@Body request: WomenCardRequest): WomenCard

    @PUT("gateway/women/CardsWomen/{id}")
    suspend fun updateCard(@Path("id") id: Int, @Body request: WomenCardRequest): WomenCard

    @DELETE("gateway/women/CardsWomen/{id}")
    suspend fun deleteCard(@Path("id") id: Int)

    @PATCH("gateway/women/CardsWomen/{id}/sale")
    suspend fun setDiscount(@Path("id") id: Int, @Body request: SetDiscountRequest)

    @GET("gateway/women/WomanCategory")
    suspend fun getCategories(): List<WomenCategory>

    @POST("gateway/women/WomanCategory")
    suspend fun createCategory(@Body body: Map<String, String>): WomenCategory

    @PUT("gateway/women/WomanCategory/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body body: Map<String, String>): WomenCategory

    @DELETE("gateway/women/WomanCategory/{id}")
    suspend fun deleteCategory(@Path("id") id: Int)
}

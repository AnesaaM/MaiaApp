package com.example.maia.network

import com.example.maia.model.MenCard
import com.example.maia.model.men.MenCardRequest
import com.example.maia.model.men.MenCategory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MenManagerApi {
    @GET("gateway/men/MenCards")
    suspend fun getAllCards(): List<MenCard>

    @POST("gateway/men/MenCards")
    suspend fun createCard(@Body request: MenCardRequest): MenCard

    @PUT("gateway/men/MenCards/{id}")
    suspend fun updateCard(@Path("id") id: Int, @Body request: MenCardRequest): MenCard

    @DELETE("gateway/men/MenCards/{id}")
    suspend fun deleteCard(@Path("id") id: Int)

    @GET("gateway/men/MenCategory")
    suspend fun getCategories(): List<MenCategory>

    @POST("gateway/men/MenCategory")
    suspend fun createCategory(@Body body: Map<String, String>): MenCategory

    @PUT("gateway/men/MenCategory/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body body: Map<String, String>): MenCategory

    @DELETE("gateway/men/MenCategory/{id}")
    suspend fun deleteCategory(@Path("id") id: Int)
}

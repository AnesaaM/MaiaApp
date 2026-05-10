package com.example.maia.network
import com.example.maia.model.KidsCards
import retrofit2.http.GET
interface KidsApi {
    @GET("api/KidsCards")
    suspend fun getKidsCards(): List<KidsCards>
}
package com.example.maia.network

import com.example.maia.model.settings.AppSettings
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SettingsApi {
    @GET("gateway/settings/settings/global")
    suspend fun getGlobalSettings(): AppSettings

    @POST("gateway/settings/settings/global")
    suspend fun saveGlobalSettings(@Body settings: AppSettings)

    @GET("gateway/settings/settings/user")
    suspend fun getUserSettings(): AppSettings

    @POST("gateway/settings/settings/user")
    suspend fun saveUserSettings(@Body settings: AppSettings)

    @DELETE("gateway/settings/settings/{id}")
    suspend fun deleteSetting(@Path("id") id: Int)
}

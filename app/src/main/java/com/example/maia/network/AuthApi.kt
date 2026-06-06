package com.example.maia.network

import com.example.maia.model.auth.AuthResponse
import com.example.maia.model.auth.ForgotPasswordRequest
import com.example.maia.model.auth.LoginRequest
import com.example.maia.model.auth.RegisterRequest
import com.example.maia.model.auth.ResetPasswordRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("gateway/auth/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("gateway/auth/auth/register")
    suspend fun register(@Body request: RegisterRequest)

    @POST("gateway/auth/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest)

    @POST("gateway/auth/auth/logout")
    suspend fun logout()

    @GET("gateway/auth/auth/me")
    suspend fun getMe(): Any
}

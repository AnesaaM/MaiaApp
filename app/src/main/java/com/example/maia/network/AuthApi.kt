package com.example.maia.network

import com.example.maia.model.auth.AuthResponse
import com.example.maia.model.auth.ForgotPasswordRequest
import com.example.maia.model.auth.LoginRequest
import com.example.maia.model.auth.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

// Adjust endpoint paths to match your backend if needed
interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest)

    @POST("api/auth/forgotPassword")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest)
}

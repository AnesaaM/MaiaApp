package com.example.maia.model.auth

data class AuthResponse(
    val token: String,
    val username: String? = null,
    val userId: Int? = null
)

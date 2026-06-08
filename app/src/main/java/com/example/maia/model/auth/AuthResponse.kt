package com.example.maia.model.auth

data class AuthResponse(
    val isLoggedIn: Boolean = false,
    val role: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val token: String? = null,
    val accessToken: String? = null
)

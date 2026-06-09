package com.example.maia.model.auth

data class UpdateMeRequest(
    val firstName: String,
    val lastName: String,
    val email: String
)

data class UpdateMeResponse(
    val firstName: String,
    val lastName: String,
    val email: String
)

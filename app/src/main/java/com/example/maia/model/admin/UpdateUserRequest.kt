package com.example.maia.model.admin

data class UpdateUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String? = null
)

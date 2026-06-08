package com.example.maia.model.admin

data class User(
    val userID: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val roleType: String = "Customer",
    val isActive: Boolean = true,
    val createdAt: String? = null
)

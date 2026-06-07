package com.example.maia.model.admin

data class CreateStaffRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val roleType: String
)

package com.example.maia.model.admin

data class ChangeRoleRequest(
    val userID: Int,
    val newRoleID: Int
)

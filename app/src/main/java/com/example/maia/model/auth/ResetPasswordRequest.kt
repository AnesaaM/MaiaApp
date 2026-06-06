package com.example.maia.model.auth

data class ResetPasswordRequest(val token: String, val newPassword: String)

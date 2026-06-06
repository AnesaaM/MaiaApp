package com.example.maia.data

import android.content.Context

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences("maia_auth", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("token", token).apply()
    fun getToken(): String? = prefs.getString("token", null)
    fun clearToken() = prefs.edit().remove("token").apply()

    fun saveUsername(name: String) = prefs.edit().putString("username", name).apply()
    fun getUsername(): String? = prefs.getString("username", null)

    fun isLoggedIn(): Boolean = getToken() != null
}

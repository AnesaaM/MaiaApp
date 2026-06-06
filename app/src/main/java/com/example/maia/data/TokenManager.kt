package com.example.maia.data

import android.content.Context

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences("maia_auth", Context.MODE_PRIVATE)

    fun saveUsername(name: String) = prefs.edit().putString("username", name).apply()
    fun getUsername(): String? = prefs.getString("username", null)

    fun saveEmail(email: String) = prefs.edit().putString("email", email).apply()
    fun getEmail(): String? = prefs.getString("email", null)

    fun saveRole(role: String) = prefs.edit().putString("role", role).apply()
    fun getRole(): String? = prefs.getString("role", null)

    fun clear() = prefs.edit().clear().apply()

    // Auth is cookie-based — logged in if we have saved user info
    fun isLoggedIn(): Boolean = getEmail() != null
}

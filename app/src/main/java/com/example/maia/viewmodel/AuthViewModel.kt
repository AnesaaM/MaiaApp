package com.example.maia.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.maia.data.TokenManager
import com.example.maia.model.auth.ForgotPasswordRequest
import com.example.maia.model.auth.LoginRequest
import com.example.maia.model.auth.RegisterRequest
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val tokenManager: TokenManager) : ViewModel() {

    var loginState = mutableStateOf<AuthState>(AuthState.Idle)
        private set
    var registerState = mutableStateOf<AuthState>(AuthState.Idle)
        private set
    var forgotPasswordState = mutableStateOf<AuthState>(AuthState.Idle)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginState.value = AuthState.Loading
            try {
                val response = RetrofitInstance.authApi.login(LoginRequest(email, password))
                tokenManager.saveToken(response.token)
                response.username?.let { tokenManager.saveUsername(it) }
                RetrofitInstance.updateToken(response.token)
                loginState.value = AuthState.Success
            } catch (e: Exception) {
                loginState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            registerState.value = AuthState.Loading
            try {
                RetrofitInstance.authApi.register(RegisterRequest(username, email, password))
                registerState.value = AuthState.Success
            } catch (e: Exception) {
                registerState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            forgotPasswordState.value = AuthState.Loading
            try {
                RetrofitInstance.authApi.forgotPassword(ForgotPasswordRequest(email))
                forgotPasswordState.value = AuthState.Success
            } catch (e: Exception) {
                forgotPasswordState.value = AuthState.Error(e.message ?: "Request failed")
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
        RetrofitInstance.updateToken(null)
        loginState.value = AuthState.Idle
    }
}

class AuthViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AuthViewModel(tokenManager) as T
}

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
                // Token vjen si cookie — OkHttp e ruan automatikisht
                tokenManager.saveEmail(response.email)
                tokenManager.saveUsername("${response.firstName} ${response.lastName}")
                tokenManager.saveRole(response.role)
                loginState.value = AuthState.Success
            } catch (e: Exception) {
                loginState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            registerState.value = AuthState.Loading
            try {
                RetrofitInstance.authApi.register(RegisterRequest(firstName, lastName, email, password))
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
        viewModelScope.launch {
            try { RetrofitInstance.authApi.logout() } catch (_: Exception) { }
            RetrofitInstance.clearSession()
            tokenManager.clear()
            loginState.value = AuthState.Idle
        }
    }
}

class AuthViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AuthViewModel(tokenManager) as T
}

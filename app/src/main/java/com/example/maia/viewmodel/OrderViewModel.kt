package com.example.maia.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.order.Order
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    var orders = mutableStateOf<List<Order>>(emptyList())
        private set
    var isLoading = mutableStateOf(false)
        private set
    var error = mutableStateOf<String?>(null)
        private set

    fun loadOrders() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                orders.value = RetrofitInstance.orderApi.getOrders()
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to load orders"
            } finally {
                isLoading.value = false
            }
        }
    }
}

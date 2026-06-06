package com.example.maia.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.cart.AddToCartRequest
import com.example.maia.model.cart.CartItem
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    var cartItems = mutableStateOf<List<CartItem>>(emptyList())
        private set
    var isLoading = mutableStateOf(false)
        private set
    var error = mutableStateOf<String?>(null)
        private set
    var orderPlaced = mutableStateOf(false)
        private set

    val totalPrice: Double
        get() = cartItems.value.sumOf { (it.product?.price ?: 0.0) * it.quantity }

    val itemCount: Int
        get() = cartItems.value.sumOf { it.quantity }

    fun loadCart() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                cartItems.value = RetrofitInstance.orderServiceApi.getCart()
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to load cart"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addToCart(productId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                RetrofitInstance.orderServiceApi.addToCart(AddToCartRequest(productId))
                onSuccess()
                loadCart()
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to add to cart"
            }
        }
    }

    fun removeFromCart(cartItemId: Int) {
        viewModelScope.launch {
            try {
                RetrofitInstance.orderServiceApi.removeFromCart(cartItemId)
                loadCart()
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to remove item"
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                RetrofitInstance.orderServiceApi.clearCart()
                cartItems.value = emptyList()
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to clear cart"
            }
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                RetrofitInstance.orderServiceApi.placeOrder()
                cartItems.value = emptyList()
                orderPlaced.value = true
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to place order"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearOrderPlaced() {
        orderPlaced.value = false
    }

    fun clearError() {
        error.value = null
    }
}

package com.example.maia.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.Product
import com.example.maia.model.cart.AddToCartRequest
import com.example.maia.model.cart.CartItem
import com.example.maia.model.cart.CartResponse
import com.example.maia.network.RetrofitInstance
import com.example.maia.model.order.Order
import com.example.maia.util.EmailService
import com.google.gson.JsonObject
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
    var placedOrder = mutableStateOf<Order?>(null)
        private set

    val totalPrice: Double
        get() = cartItems.value.sumOf { it.price * it.quantity }

    val itemCount: Int
        get() = cartItems.value.sumOf { it.quantity }

    fun loadCart() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                cartItems.value = RetrofitInstance.orderServiceApi.getCart().items
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to load cart"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addToCart(
        product: Product,
        productSource: String,
        size: String? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val placeholder = CartItem(
            id = -product.id,
            productId = product.id,
            productName = product.title,
            imageUrl = product.imageUrl,
            price = product.price,
            quantity = 1,
            size = size
        )
        cartItems.value = cartItems.value + placeholder
        onSuccess()

        viewModelScope.launch {
            try {
                RetrofitInstance.orderServiceApi.addToCart(
                    AddToCartRequest(
                        productId = product.id,
                        productSource = productSource,
                        productName = product.title,
                        imageUrl = product.imageUrl,
                        price = product.price,
                        size = size
                    )
                )
                loadCart()
            } catch (e: Exception) {
                cartItems.value = cartItems.value.filter { it.id != -product.id }
                error.value = e.message
                onError(e.message ?: "Failed to add to cart")
            }
        }
    }

    fun removeFromCart(cartItemId: Int) {
        val removed = cartItems.value.find { it.id == cartItemId }
        cartItems.value = cartItems.value.filter { it.id != cartItemId }
        viewModelScope.launch {
            try {
                RetrofitInstance.orderServiceApi.removeFromCart(cartItemId)
            } catch (e: Exception) {
                if (removed != null) cartItems.value = cartItems.value + removed
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

    fun placeOrder(email: String = "", name: String = "") {
        val itemsSnapshot = cartItems.value.toList()
        viewModelScope.launch {
            isLoading.value = true
            try {
                val order = RetrofitInstance.orderServiceApi.placeOrder(JsonObject())
                placedOrder.value = order
                cartItems.value = emptyList()
                orderPlaced.value = true
                if (email.isNotBlank()) {
                    val orderRef = "MAIA-${order.id.toString().padStart(6, '0')}"
                    launch {
                        EmailService.sendInvoice(email, name, orderRef, order.totalAmount, itemsSnapshot)
                    }
                }
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to place order"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearOrderPlaced() {
        orderPlaced.value = false
        placedOrder.value = null
    }

    fun clearError() {
        error.value = null
    }
}

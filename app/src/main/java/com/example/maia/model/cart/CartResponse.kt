package com.example.maia.model.cart

data class CartResponse(
    val cartId: Int = 0,
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0
)

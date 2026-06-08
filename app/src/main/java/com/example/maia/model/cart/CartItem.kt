package com.example.maia.model.cart

data class CartItem(
    val id: Int,
    val productId: Int,
    val productName: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val size: String? = null
)

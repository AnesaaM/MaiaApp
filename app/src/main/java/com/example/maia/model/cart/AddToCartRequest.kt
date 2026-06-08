package com.example.maia.model.cart

data class AddToCartRequest(
    val productId: Int,
    val productSource: String,
    val productName: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int = 1,
    val size: String? = null
)

package com.example.maia.model.cart

import com.example.maia.model.Product

data class CartItem(
    val id: Int,
    val productId: Int,
    val quantity: Int,
    val product: Product? = null
)

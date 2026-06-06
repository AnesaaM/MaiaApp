package com.example.maia.model.cart

import com.example.maia.model.KidsCards

data class CartItem(
    val id: Int,
    val productId: Int,
    val quantity: Int,
    val product: KidsCards? = null
)

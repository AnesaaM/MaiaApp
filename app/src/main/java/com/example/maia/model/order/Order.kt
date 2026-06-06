package com.example.maia.model.order

import com.example.maia.model.KidsCards

data class Order(
    val id: Int,
    val totalAmount: Double,
    val status: String,
    val createdAt: String,
    val items: List<OrderItem>? = null
)

data class OrderItem(
    val id: Int,
    val productId: Int,
    val quantity: Int,
    val price: Double,
    val product: KidsCards? = null
)

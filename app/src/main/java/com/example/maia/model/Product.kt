package com.example.maia.model

data class Product(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val price: Double,
    val description: String = ""
)

fun KidsCards.toProduct() = Product(id, title, imageUrl, price, description)
fun WomenCard.toProduct() = Product(id, title, imageUrl, price, description)
fun MenCard.toProduct() = Product(id, title, imageUrl, price, description)

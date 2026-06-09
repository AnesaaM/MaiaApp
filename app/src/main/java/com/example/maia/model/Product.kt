package com.example.maia.model

data class Product(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val price: Double,
    val description: String = "",
    val color: String = "",
    val category: String = "",
    val discountPercent: Int? = null,
    val categoryId: Int = 0
)

fun KidsCards.toProduct() = Product(id, title, imageUrl, price, description, color ?: "", discountPercent = discountPercent, categoryId = kidsCategoryId)
fun WomenCard.toProduct() = Product(id, title, imageUrl, price, description ?: "", color ?: "", category ?: "", discountPercent, womanCategoryId)
fun MenCard.toProduct() = Product(id, title, imageUrl, price, description, color ?: "", menCategoryName, discountPercent, menCategoryId)

package com.example.maia.model

data class WomenCard(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val price: Double,
    val description: String? = null,
    val category: String? = null,
    val color: String? = null,
    val womanCategoryId: Int = 0,
    val discountPercent: Int? = null
)

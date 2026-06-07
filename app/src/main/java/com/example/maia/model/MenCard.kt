package com.example.maia.model

data class MenCard(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val price: Double,
    val description: String = "",
    val color: String = "",
    val menCategoryId: Int = 0,
    val menCategoryName: String = "",
    val discountPercent: Int? = null
)

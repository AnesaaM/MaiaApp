package com.example.maia.model.men

data class MenCardRequest(
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val menCategoryId: Int,
    val color: String? = null,
    val discountPercent: Int? = null
)

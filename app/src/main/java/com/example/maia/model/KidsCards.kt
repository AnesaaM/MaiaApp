package com.example.maia.model

data class KidsCards(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val price: Double,
    val description: String,
    val color: String? = null,
    val discountPercent: Int? = null,
    val kidsCategoryId: Int = 0
)
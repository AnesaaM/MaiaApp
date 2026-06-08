package com.example.maia.model.women

data class WomenCardRequest(
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val womanCategoryId: Int,
    val color: String? = null
)

package com.example.maia.model

data class WomenCard(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val price: Double,
    val description: String = "",
    val category: String = ""
)

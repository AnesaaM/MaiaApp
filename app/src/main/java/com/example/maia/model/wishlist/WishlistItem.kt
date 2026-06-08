package com.example.maia.model.wishlist

data class WishlistItem(
    val id: Int,
    val productId: Int,
    val productName: String? = null,
    val productImage: String? = null,
    val price: Double? = null
)

data class AddToWishlistRequest(val productId: Int)

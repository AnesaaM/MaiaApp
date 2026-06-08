package com.example.maia.model.wishlist

data class WishlistResponse(
    val wishlistId: Int = 0,
    val items: List<WishlistItem> = emptyList()
)

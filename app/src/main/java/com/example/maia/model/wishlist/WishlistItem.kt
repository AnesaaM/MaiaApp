package com.example.maia.model.wishlist

import com.example.maia.model.Product

data class WishlistItem(
    val id: Int,
    val productId: Int,
    val product: Product? = null
)

data class AddToWishlistRequest(val productId: Int)

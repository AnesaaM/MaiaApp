package com.example.maia.model.wishlist

import com.example.maia.model.KidsCards

data class WishlistItem(
    val id: Int,
    val productId: Int,
    val product: KidsCards? = null
)

data class AddToWishlistRequest(val productId: Int)

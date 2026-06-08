package com.example.maia.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.wishlist.AddToWishlistRequest
import com.example.maia.model.wishlist.WishlistItem
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class WishlistViewModel : ViewModel() {

    var wishlistItems = mutableStateOf<List<WishlistItem>>(emptyList())
        private set
    var isLoading = mutableStateOf(false)
        private set
    var error = mutableStateOf<String?>(null)
        private set

    fun isWishlisted(productId: Int): Boolean =
        wishlistItems.value.any { it.productId == productId }

    fun loadWishlist() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                wishlistItems.value = RetrofitInstance.wishlistApi.getWishlist().items
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to load wishlist"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun toggleWishlist(
        productId: Int,
        productName: String = "",
        productImage: String = "",
        price: Double = 0.0
    ) {
        val existing = wishlistItems.value.find { it.productId == productId }
        if (existing != null) {
            wishlistItems.value = wishlistItems.value.filter { it.productId != productId }
            viewModelScope.launch {
                try {
                    RetrofitInstance.wishlistApi.removeFromWishlist(existing.id)
                } catch (e: Exception) {
                    wishlistItems.value = wishlistItems.value + existing
                    error.value = e.message ?: "Failed to remove from wishlist"
                }
            }
        } else {
            val placeholder = WishlistItem(
                id = -productId,
                productId = productId,
                productName = productName.ifEmpty { null },
                productImage = productImage.ifEmpty { null },
                price = if (price > 0) price else null
            )
            wishlistItems.value = wishlistItems.value + placeholder
            viewModelScope.launch {
                try {
                    RetrofitInstance.wishlistApi.addToWishlist(AddToWishlistRequest(productId))
                    val confirmed = RetrofitInstance.wishlistApi.getWishlist().items
                        .find { it.productId == productId }
                    if (confirmed != null) {
                        wishlistItems.value = wishlistItems.value.map {
                            if (it.productId == productId && it.id == -productId)
                                it.copy(id = confirmed.id)
                            else it
                        }
                    }
                } catch (e: Exception) {
                    wishlistItems.value = wishlistItems.value.filter { it.productId != productId }
                    error.value = e.message ?: "Failed to add to wishlist"
                }
            }
        }
    }

    fun removeFromWishlist(id: Int) {
        val removed = wishlistItems.value.find { it.id == id }
        wishlistItems.value = wishlistItems.value.filter { it.id != id }
        viewModelScope.launch {
            try {
                RetrofitInstance.wishlistApi.removeFromWishlist(id)
            } catch (e: Exception) {
                if (removed != null) wishlistItems.value = wishlistItems.value + removed
                error.value = e.message ?: "Failed to remove item"
            }
        }
    }
}

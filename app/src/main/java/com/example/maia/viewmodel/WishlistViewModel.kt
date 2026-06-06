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
                wishlistItems.value = RetrofitInstance.wishlistApi.getWishlist()
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to load wishlist"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun toggleWishlist(productId: Int) {
        viewModelScope.launch {
            try {
                val existing = wishlistItems.value.find { it.productId == productId }
                if (existing != null) {
                    RetrofitInstance.wishlistApi.removeFromWishlist(existing.id)
                } else {
                    RetrofitInstance.wishlistApi.addToWishlist(AddToWishlistRequest(productId))
                }
                loadWishlist()
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to update wishlist"
            }
        }
    }

    fun removeFromWishlist(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitInstance.wishlistApi.removeFromWishlist(id)
                loadWishlist()
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to remove item"
            }
        }
    }
}

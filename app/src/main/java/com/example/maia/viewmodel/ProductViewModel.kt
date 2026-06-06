package com.example.maia.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.KidsCards
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    var allProducts = mutableStateOf<List<KidsCards>>(emptyList())
        private set
    var searchQuery = mutableStateOf("")
        private set
    var isLoading = mutableStateOf(true)
        private set
    var error = mutableStateOf<String?>(null)
        private set

    val filteredProducts: List<KidsCards>
        get() {
            val q = searchQuery.value.trim().lowercase()
            return if (q.isEmpty()) allProducts.value
            else allProducts.value.filter {
                it.title.lowercase().contains(q) || it.description.lowercase().contains(q)
            }
        }

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                allProducts.value = RetrofitInstance.api.getKidsCards()
            } catch (e: Exception) {
                error.value = e.message ?: "Failed to load products"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateSearch(query: String) {
        searchQuery.value = query
    }
}

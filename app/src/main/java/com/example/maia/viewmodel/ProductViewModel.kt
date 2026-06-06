package com.example.maia.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.Product
import com.example.maia.model.Section
import com.example.maia.model.toProduct
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    var currentSection = mutableStateOf(Section.WOMAN)
        private set
    var allProducts = mutableStateOf<List<Product>>(emptyList())
        private set
    var searchQuery = mutableStateOf("")
        private set
    var isLoading = mutableStateOf(true)
        private set
    var error = mutableStateOf<String?>(null)
        private set

    val filteredProducts: List<Product>
        get() {
            val q = searchQuery.value.trim().lowercase()
            return if (q.isEmpty()) allProducts.value
            else allProducts.value.filter {
                it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q)
            }
        }

    init {
        loadProducts()
    }

    fun switchSection(section: Section) {
        if (currentSection.value != section) {
            currentSection.value = section
            searchQuery.value = ""
            loadProducts()
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                allProducts.value = when (currentSection.value) {
                    Section.WOMAN -> RetrofitInstance.womenApi.getWomenCards().map { it.toProduct() }
                    Section.MAN   -> RetrofitInstance.menApi.getMenCards().map { it.toProduct() }
                    Section.KIDS  -> RetrofitInstance.kidsApi.getKidsCards().map { it.toProduct() }
                }
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

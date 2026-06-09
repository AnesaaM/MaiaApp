package com.example.maia.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.KidsCards
import com.example.maia.model.kids.KidsCategory
import com.example.maia.model.kids.KidsProductType
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class KidsManagerViewModel : ViewModel() {
    var cards by mutableStateOf<List<KidsCards>>(emptyList()); private set
    var categories by mutableStateOf<List<KidsCategory>>(emptyList()); private set
    var productTypes by mutableStateOf<List<KidsProductType>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            isLoading = true; error = null
            try {
                cards = RetrofitInstance.kidsApi.getKidsCards()
                categories = RetrofitInstance.kidsApi.getCategories()
                productTypes = RetrofitInstance.kidsApi.getProductTypes()
            } catch (e: Exception) {
                error = e.message ?: "Failed to load"
            } finally { isLoading = false }
        }
    }

    fun setDiscount(id: Int, pct: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.setKidsDiscount(id, mapOf("discountPercent" to pct)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateCard(id: Int, title: String, price: Double, imageUrl: String, description: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitInstance.kidsApi.updateKidsCard(id, mapOf("title" to title, "price" to price, "imageUrl" to imageUrl, "description" to description))
                loadAll(); onDone(null)
            } catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteCard(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.deleteKidsCard(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun createCategory(name: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.createCategory(mapOf("name" to name)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateCategory(id: Int, name: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.updateCategory(id, mapOf("name" to name)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteCategory(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.deleteCategory(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun createProductType(name: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.createProductType(mapOf("name" to name)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateProductType(id: Int, name: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.updateProductType(id, mapOf("name" to name)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteProductType(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.deleteProductType(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }
}

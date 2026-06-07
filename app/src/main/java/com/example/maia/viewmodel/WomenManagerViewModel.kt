package com.example.maia.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.WomenCard
import com.example.maia.model.women.SetDiscountRequest
import com.example.maia.model.women.WomenCardRequest
import com.example.maia.model.women.WomenCategory
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class WomenManagerViewModel : ViewModel() {
    var cards by mutableStateOf<List<WomenCard>>(emptyList()); private set
    var categories by mutableStateOf<List<WomenCategory>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            isLoading = true; error = null
            try {
                cards = RetrofitInstance.womenManagerApi.getAllCards()
                categories = RetrofitInstance.womenManagerApi.getCategories()
            } catch (e: Exception) {
                error = e.message ?: "Failed to load"
            } finally { isLoading = false }
        }
    }

    fun createCard(req: WomenCardRequest, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.createCard(req); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateCard(id: Int, req: WomenCardRequest, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.updateCard(id, req); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteCard(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.deleteCard(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun setDiscount(id: Int, pct: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.setDiscount(id, SetDiscountRequest(pct)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun createCategory(name: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.createCategory(mapOf("name" to name)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateCategory(id: Int, name: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.updateCategory(id, mapOf("name" to name)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteCategory(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.deleteCategory(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }
}

package com.example.maia.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.MenCard
import com.example.maia.model.men.MenCardRequest
import com.example.maia.model.men.MenCategory
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class MenManagerViewModel : ViewModel() {
    var cards by mutableStateOf<List<MenCard>>(emptyList()); private set
    var categories by mutableStateOf<List<MenCategory>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            isLoading = true; error = null
            try {
                cards = RetrofitInstance.menManagerApi.getAllCards()
                categories = RetrofitInstance.menManagerApi.getCategories()
            } catch (e: Exception) {
                error = e.message ?: "Failed to load"
            } finally { isLoading = false }
        }
    }

    fun createCard(req: MenCardRequest, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.menManagerApi.createCard(req); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateCard(id: Int, req: MenCardRequest, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.menManagerApi.updateCard(id, req); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteCard(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.menManagerApi.deleteCard(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun setDiscount(card: MenCard, pct: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitInstance.menManagerApi.updateCard(card.id, MenCardRequest(
                    title = card.title, description = card.description,
                    price = card.price, imageUrl = card.imageUrl,
                    menCategoryId = card.menCategoryId, color = card.color,
                    discountPercent = pct
                ))
                loadAll(); onDone(null)
            } catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun createCategory(name: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.menManagerApi.createCategory(mapOf("name" to name)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun updateCategory(id: Int, name: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.menManagerApi.updateCategory(id, mapOf("name" to name)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun deleteCategory(id: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.menManagerApi.deleteCategory(id); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }
}

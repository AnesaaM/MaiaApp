package com.example.maia.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.KidsCards
import com.example.maia.model.MenCard
import com.example.maia.model.WomenCard
import com.example.maia.model.men.MenCardRequest
import com.example.maia.model.women.SetDiscountRequest
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

data class SaleItem(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val price: Double,
    val discountPercent: Int,
    val section: String
)

class SalesManagerViewModel : ViewModel() {
    var womenCards by mutableStateOf<List<WomenCard>>(emptyList()); private set
    var menCards by mutableStateOf<List<MenCard>>(emptyList()); private set
    var kidsCards by mutableStateOf<List<KidsCards>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            isLoading = true; error = null
            try {
                womenCards = RetrofitInstance.womenManagerApi.getAllCards()
                menCards = RetrofitInstance.menManagerApi.getAllCards()
                kidsCards = RetrofitInstance.kidsApi.getKidsCards()
            } catch (e: Exception) {
                error = e.message ?: "Failed to load"
            } finally { isLoading = false }
        }
    }

    fun setWomenDiscount(id: Int, pct: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.womenManagerApi.setDiscount(id, SetDiscountRequest(pct)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    fun setMenDiscount(card: MenCard, pct: Int, onDone: (String?) -> Unit) {
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

    fun setKidsDiscount(id: Int, pct: Int, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try { RetrofitInstance.kidsApi.setKidsDiscount(id, mapOf("discountPercent" to pct)); loadAll(); onDone(null) }
            catch (e: Exception) { onDone(e.message ?: "Failed") }
        }
    }

    val allOnSale: List<SaleItem> get() =
        womenCards.filter { (it.discountPercent ?: 0) > 0 }
            .map { SaleItem(it.id, it.title, it.imageUrl, it.price, it.discountPercent!!, "WOMEN") } +
        menCards.filter { (it.discountPercent ?: 0) > 0 }
            .map { SaleItem(it.id, it.title, it.imageUrl, it.price, it.discountPercent!!, "MEN") } +
        kidsCards.filter { (it.discountPercent ?: 0) > 0 }
            .map { SaleItem(it.id, it.title, it.imageUrl, it.price, it.discountPercent!!, "KIDS") }
}

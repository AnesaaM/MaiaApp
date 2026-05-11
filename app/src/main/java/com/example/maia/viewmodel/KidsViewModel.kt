package com.example.maia.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maia.model.KidsCards
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

class KidsViewModel : ViewModel() {

    var cards = mutableStateOf<List<KidsCards>>(emptyList())
        private set

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                cards.value = RetrofitInstance.api.getKidsCards()
            } catch (e: Exception) {
                Log.e("KidsViewModel", e.toString())
            }
        }
    }
}
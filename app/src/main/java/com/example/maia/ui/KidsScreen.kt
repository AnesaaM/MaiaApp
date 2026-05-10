package com.example.maia.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.maia.model.KidsCards
import com.example.maia.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun KidsScreen() {

    var kidsCards by remember { mutableStateOf<List<KidsCards>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                errorMessage = null

                kidsCards = RetrofitInstance.api.getKidsCards()

            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        when {

            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            kidsCards.isEmpty() -> {
                Text(
                    text = "S'ka të dhëna nga API",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn {

                    items(kidsCards) { card ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {

                            Column(modifier = Modifier.padding(12.dp)) {

                                Text(text = card.title)
                                Text(text = "€ ${card.price}")
                            }
                        }
                    }
                }
            }
        }
    }
}
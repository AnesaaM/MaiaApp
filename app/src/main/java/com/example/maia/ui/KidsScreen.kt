package com.example.maia.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
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
                kidsCards = RetrofitInstance.api.getKidsCards()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        when {

            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }

            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
            }

            else -> {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp)
                ) {

                    items(kidsCards) { card ->

                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {

                            Column(modifier = Modifier.padding(12.dp)) {

                                Text(text = card.title)

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "€ ${card.price}",
                                    color = Color(0xFF6C5CE7)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
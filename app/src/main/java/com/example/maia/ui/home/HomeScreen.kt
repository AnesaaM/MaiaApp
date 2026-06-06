package com.example.maia.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.maia.model.KidsCards
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.BlobHeader
import com.example.maia.ui.components.MaiaAccent
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.util.NotificationHelper
import com.example.maia.data.TokenManager
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory
import com.example.maia.viewmodel.CartViewModel
import com.example.maia.viewmodel.ProductViewModel
import com.example.maia.viewmodel.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    tokenManager: TokenManager,
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel
) {
    val productVm: ProductViewModel = viewModel()
    val context = LocalContext.current

    val products = productVm.filteredProducts
    val searchQuery = productVm.searchQuery.value
    val isLoading = productVm.isLoading.value
    val error = productVm.error.value

    var showSearch by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        wishlistViewModel.loadWishlist()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        // Header blob + MAIA logo
        BlobHeader(height = 190.dp)

        // Category tabs row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("WOMAN", "MAN", "KIDS").forEachIndexed { index, label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        label,
                        fontSize = 12.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = if (index == 2) FontWeight.Bold else FontWeight.Normal,
                        color = MaiaText
                    )
                    if (index == 2) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(MaiaText, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { showSearch = !showSearch },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MaiaText)
            }
        }

        // Search bar (shown when search icon tapped)
        if (showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { productVm.updateSearch(it) },
                placeholder = { Text("WHAT ARE YOU LOOKING FOR?", fontSize = 11.sp, letterSpacing = 1.sp, color = MaiaTextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFDDD0CA),
                    focusedBorderColor = MaiaText,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
        }

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaiaText, strokeWidth = 1.5.dp)
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Failed to load products", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { productVm.loadProducts() }) {
                        Text("RETRY", letterSpacing = 1.sp, color = MaiaText, fontSize = 11.sp)
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products) { card ->
                        FashionProductCard(
                            card = card,
                            isWishlisted = wishlistViewModel.isWishlisted(card.id),
                            onAddToCart = {
                                cartViewModel.addToCart(card.id) {
                                    NotificationHelper.showCartNotification(context, card.title)
                                }
                            },
                            onToggleWishlist = { wishlistViewModel.toggleWishlist(card.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FashionProductCard(
    card: KidsCards,
    isWishlisted: Boolean,
    onAddToCart: () -> Unit,
    onToggleWishlist: () -> Unit
) {
    Column {
        Box {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = card.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFEDE8E3)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onToggleWishlist,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(32.dp)
            ) {
                Icon(
                    if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isWishlisted) Color(0xFF8B1A1A) else MaiaText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            card.title,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp,
            color = MaiaText,
            maxLines = 1
        )
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${String.format("%.0f", card.price)} EUR",
                fontSize = 11.sp,
                color = MaiaTextSecondary
            )
            TextButton(
                onClick = onAddToCart,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(20.dp)
            ) {
                Text("+", fontSize = 16.sp, color = MaiaText, fontWeight = FontWeight.Light)
            }
        }
    }
}

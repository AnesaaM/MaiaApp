package com.example.maia.ui.wishlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
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
import coil.compose.AsyncImage
import com.example.maia.model.wishlist.WishlistItem
import com.example.maia.util.NotificationHelper
import com.example.maia.viewmodel.CartViewModel
import com.example.maia.viewmodel.WishlistViewModel

private val Purple = Color(0xFF6C5CE7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(wishlistViewModel: WishlistViewModel, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val items = wishlistViewModel.wishlistItems.value
    val isLoading = wishlistViewModel.isLoading.value

    LaunchedEffect(Unit) { wishlistViewModel.loadWishlist() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Wishlist", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Purple)
                items.isEmpty() -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your wishlist is empty", fontSize = 18.sp, color = Color.Gray)
                    Text("Tap the heart icon on products to save them", fontSize = 14.sp, color = Color.LightGray)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        WishlistItemRow(
                            item = item,
                            onRemove = { wishlistViewModel.removeFromWishlist(item.id) },
                            onAddToCart = {
                                cartViewModel.addToCart(item.productId) {
                                    NotificationHelper.showCartNotification(
                                        context,
                                        item.product?.title ?: "Item"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WishlistItemRow(
    item: WishlistItem,
    onRemove: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product?.imageUrl,
                contentDescription = item.product?.title,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.product?.title ?: "Product #${item.productId}",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                if (item.product != null) {
                    Spacer(Modifier.height(4.dp))
                    Text("€ ${String.format("%.2f", item.product.price)}", color = Purple, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add to Cart", fontSize = 12.sp)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

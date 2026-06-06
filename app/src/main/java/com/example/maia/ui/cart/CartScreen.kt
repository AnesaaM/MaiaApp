package com.example.maia.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maia.model.cart.CartItem
import com.example.maia.model.wishlist.WishlistItem
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaButton
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.util.NotificationHelper
import com.example.maia.viewmodel.CartViewModel
import com.example.maia.viewmodel.WishlistViewModel

@Preview(showBackground = true, name = "Cart Screen")
@Composable
fun CartScreenPreview() {
    CartScreen(cartViewModel = CartViewModel(), wishlistViewModel = WishlistViewModel())
}

@Composable
fun CartScreen(cartViewModel: CartViewModel, wishlistViewModel: WishlistViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val blobColor = MaiaBlob

    val cartItems = cartViewModel.cartItems.value
    val cartLoading = cartViewModel.isLoading.value
    val orderPlaced = cartViewModel.orderPlaced.value
    val wishlistItems = wishlistViewModel.wishlistItems.value
    val wishlistLoading = wishlistViewModel.isLoading.value

    LaunchedEffect(Unit) {
        cartViewModel.loadCart()
        wishlistViewModel.loadWishlist()
    }

    LaunchedEffect(orderPlaced) {
        if (orderPlaced) {
            NotificationHelper.showOrderConfirmationNotification(context)
            cartViewModel.clearOrderPlaced()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        // Header blob
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .drawBehind {
                    val w = size.width; val h = size.height
                    val path = Path().apply {
                        moveTo(0f, 0f); lineTo(w, 0f)
                        lineTo(w, h * 0.68f)
                        cubicTo(w * 0.82f, h * 1.05f, w * 0.60f, h * 0.72f, w * 0.44f, h * 0.90f)
                        cubicTo(w * 0.28f, h * 1.08f, w * 0.12f, h * 0.78f, 0f, h * 0.85f)
                        close()
                    }
                    drawPath(path, blobColor)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SHOPPING BAG tab
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { selectedTab = 0 }
                ) {
                    Text(
                        "SHOPPING BAG",
                        fontSize = 14.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Light,
                        color = if (selectedTab == 0) MaiaText else MaiaTextSecondary
                    )
                    if (selectedTab == 0) {
                        Spacer(Modifier.width(4.dp))
                        Text("■", fontSize = 8.sp, color = MaiaText)
                    }
                }

                Spacer(Modifier.width(20.dp))

                // FAVORITES tab
                Text(
                    "FAVORITES",
                    fontSize = 14.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Light,
                    color = if (selectedTab == 1) MaiaText else MaiaTextSecondary,
                    modifier = Modifier.clickable { selectedTab = 1 }
                )

                Spacer(Modifier.weight(1f))

                // MAIA italic
                Text(
                    "MAIA",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = MaiaText,
                    letterSpacing = 2.sp
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> ShoppingBagContent(
                cartItems = cartItems,
                isLoading = cartLoading,
                total = cartViewModel.totalPrice,
                onRemove = { cartViewModel.removeFromCart(it) },
                onPlaceOrder = { cartViewModel.placeOrder() }
            )
            1 -> FavoritesContent(
                wishlistItems = wishlistItems,
                isLoading = wishlistLoading,
                onRemove = { wishlistViewModel.removeFromWishlist(it) },
                onAddToCart = { productId, name ->
                    cartViewModel.addToCart(productId) {
                        NotificationHelper.showCartNotification(context, name)
                    }
                }
            )
        }
    }
}

@Composable
private fun ShoppingBagContent(
    cartItems: List<CartItem>,
    isLoading: Boolean,
    total: Double,
    onRemove: (Int) -> Unit,
    onPlaceOrder: () -> Unit
) {
    when {
        isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaiaText, strokeWidth = 1.5.dp)
        }
        cartItems.isEmpty() -> Box(
            Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                "YOUR BAG IS EMPTY",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MaiaTextSecondary
            )
        }
        else -> Column(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cartItems, key = { it.id }) { item ->
                    CartItemRow(item = item, onRemove = { onRemove(item.id) })
                    HorizontalDivider(color = Color(0xFFEDE8E3), thickness = 0.5.dp)
                }
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TOTAL", fontSize = 10.sp, letterSpacing = 2.sp, color = MaiaTextSecondary)
                    Text(
                        "${String.format("%.0f", total)} EUR",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaiaText
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onPlaceOrder,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaiaButton)
                ) {
                    Text("PLACE ORDER", letterSpacing = 2.sp, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(item: CartItem, onRemove: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        AsyncImage(
            model = item.product?.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(80.dp).background(Color(0xFFEDE8E3)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product?.title ?: "Product #${item.productId}", fontSize = 12.sp, letterSpacing = 0.5.sp, color = MaiaText)
            Spacer(Modifier.height(4.dp))
            Text("Qty ${item.quantity}", fontSize = 11.sp, color = MaiaTextSecondary)
            if (item.product != null) {
                Spacer(Modifier.height(4.dp))
                Text("${String.format("%.0f", item.product.price)} EUR", fontSize = 12.sp, color = MaiaText)
            }
            Spacer(Modifier.height(8.dp))
            Row {
                TextButton(onClick = onRemove, contentPadding = PaddingValues(0.dp)) {
                    Text("DELETE", fontSize = 10.sp, letterSpacing = 1.sp, color = MaiaTextSecondary)
                }
                Text(" | ", fontSize = 10.sp, color = MaiaTextSecondary)
                TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                    Text("SAVE", fontSize = 10.sp, letterSpacing = 1.sp, color = MaiaTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun FavoritesContent(
    wishlistItems: List<WishlistItem>,
    isLoading: Boolean,
    onRemove: (Int) -> Unit,
    onAddToCart: (Int, String) -> Unit
) {
    when {
        isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaiaText, strokeWidth = 1.5.dp)
        }
        wishlistItems.isEmpty() -> Box(
            Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text("YOUR FAVORITES IS EMPTY", fontSize = 10.sp, letterSpacing = 2.sp, color = MaiaTextSecondary)
        }
        else -> LazyColumn(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(wishlistItems, key = { it.id }) { item ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    AsyncImage(
                        model = item.product?.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).background(Color(0xFFEDE8E3)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.product?.title ?: "Product #${item.productId}", fontSize = 12.sp, color = MaiaText)
                        if (item.product != null) {
                            Spacer(Modifier.height(4.dp))
                            Text("${String.format("%.0f", item.product.price)} EUR", fontSize = 11.sp, color = MaiaTextSecondary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row {
                            TextButton(onClick = { onRemove(item.id) }, contentPadding = PaddingValues(0.dp)) {
                                Text("REMOVE", fontSize = 10.sp, letterSpacing = 1.sp, color = MaiaTextSecondary)
                            }
                            Text(" | ", fontSize = 10.sp, color = MaiaTextSecondary)
                            TextButton(
                                onClick = { onAddToCart(item.productId, item.product?.title ?: "") },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("ADD TO BAG", fontSize = 10.sp, letterSpacing = 1.sp, color = MaiaText)
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFEDE8E3), thickness = 0.5.dp)
            }
        }
    }
}

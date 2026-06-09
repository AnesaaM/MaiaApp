package com.example.maia.ui.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.example.maia.model.Product
import com.example.maia.model.wishlist.WishlistItem
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaBorder
import com.example.maia.ui.components.MaiaButton
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.ui.components.SizePickerSheet
import com.example.maia.util.NotificationHelper
import com.example.maia.viewmodel.CartViewModel
import com.example.maia.viewmodel.WishlistViewModel

@Preview(showBackground = true, name = "Wishlist Screen")
@Composable
fun WishlistScreenPreview() {
    WishlistScreen(wishlistViewModel = WishlistViewModel(), cartViewModel = CartViewModel())
}

@Composable
fun WishlistScreen(wishlistViewModel: WishlistViewModel, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val items = wishlistViewModel.wishlistItems.value
    val isLoading = wishlistViewModel.isLoading.value
    val blobColor = MaiaBlob

    LaunchedEffect(Unit) { wishlistViewModel.loadWishlist() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        // Blob header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
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
                Spacer(Modifier.weight(1f))
                Text(
                    "MAIA",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = MaiaText,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "FAVOURITES",
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium,
                color = MaiaTextSecondary
            )
            if (items.isNotEmpty()) {
                Text(
                    "${items.size} item${if (items.size != 1) "s" else ""}",
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = MaiaTextSecondary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaiaText, strokeWidth = 1.5.dp)
            }
            items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = MaiaBlob,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Your favourites is empty",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        color = MaiaText,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Tap ♡ on any product to save it here",
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = MaiaTextSecondary
                    )
                }
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    WishlistRow(
                        item = item,
                        onRemove = { wishlistViewModel.removeFromWishlist(item.id) },
                        onAddToCart = { size ->
                            val name = item.productName ?: return@WishlistRow
                            val product = Product(
                                id = item.productId,
                                title = name,
                                imageUrl = item.productImage ?: "",
                                price = item.price ?: 0.0
                            )
                            cartViewModel.addToCart(
                                product = product,
                                productSource = "women",
                                size = size,
                                onSuccess = { NotificationHelper.showCartNotification(context, name) }
                            )
                        }
                    )
                    HorizontalDivider(color = MaiaBorder, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun WishlistRow(
    item: WishlistItem,
    onRemove: () -> Unit,
    onAddToCart: (String) -> Unit
) {
    var showSizePicker by remember { mutableStateOf(false) }

    if (showSizePicker) {
        SizePickerSheet(
            productName = item.productName ?: "",
            onDismiss = { showSizePicker = false },
            onAddToCart = { size -> onAddToCart(size); showSizePicker = false }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.productImage,
            contentDescription = item.productName,
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFEDE8E3)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.productName ?: "Product #${item.productId}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = MaiaText,
                maxLines = 2,
                lineHeight = 18.sp
            )
            if (item.price != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "€${"%.0f".format(item.price)}",
                    fontSize = 12.sp,
                    color = MaiaTextSecondary
                )
            }
            Spacer(Modifier.height(10.dp))
            TextButton(
                onClick = { showSizePicker = true },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(24.dp)
            ) {
                Text(
                    "+ ADD TO BAG",
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    color = MaiaText,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        TextButton(
            onClick = onRemove,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Text("✕", fontSize = 14.sp, color = MaiaTextSecondary)
        }
    }
}

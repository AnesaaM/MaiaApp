package com.example.maia.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.maia.model.Product
import com.example.maia.model.Section
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.BlobHeader
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.ui.components.SizePickerSheet
import com.example.maia.util.NotificationHelper
import com.example.maia.data.TokenManager
import com.example.maia.viewmodel.CartViewModel
import com.example.maia.viewmodel.ProductViewModel
import com.example.maia.viewmodel.WishlistViewModel
import kotlinx.coroutines.launch

private val sections = listOf("WOMAN" to Section.WOMAN, "MAN" to Section.MAN, "KIDS" to Section.KIDS)

@Preview(showBackground = true, name = "Shop Screen")
@Composable
fun ShopScreenPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    ShopScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        tokenManager = com.example.maia.data.TokenManager(context),
        cartViewModel = CartViewModel(),
        wishlistViewModel = WishlistViewModel()
    )
}

@Composable
fun ShopScreen(
    navController: NavController,
    tokenManager: TokenManager,
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel,
    initialSection: Int = 0,
    categoryFilter: Int = 0
) {
    val productVm: ProductViewModel = viewModel()
    val context = LocalContext.current

    val isLoading = productVm.isLoading.value
    val error = productVm.error.value

    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val pagerState = rememberPagerState(initialPage = initialSection, pageCount = { sections.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { wishlistViewModel.loadWishlist() }

    LaunchedEffect(pagerState.currentPage) {
        productVm.switchSection(sections[pagerState.currentPage].second)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        BlobHeader(
            leading = {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaiaText, modifier = Modifier.size(20.dp))
                }
            },
            actions = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showSearch = !showSearch }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaiaText, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { navController.navigate(Screen.Account.route) }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Person, contentDescription = "Account", tint = MaiaText, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaiaText, modifier = Modifier.size(20.dp))
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            sections.forEachIndexed { index, (label, _) ->
                val selected = pagerState.currentPage == index
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        productVm.switchSection(sections[index].second)
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                ) {
                    Text(
                        label,
                        fontSize = 12.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) MaiaText else MaiaTextSecondary
                    )
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(MaiaText, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }

        if (showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    productVm.updateSearch(it)
                },
                placeholder = {
                    Text("WHAT ARE YOU LOOKING FOR?", fontSize = 11.sp, letterSpacing = 1.sp, color = MaiaTextSecondary)
                },
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

        val displayProducts = if (categoryFilter == 0) productVm.filteredProducts
            else productVm.filteredProducts.filter { it.categoryId == categoryFilter }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
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
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayProducts) { product ->
                        ProductCard(
                            product = product,
                            isWishlisted = wishlistViewModel.isWishlisted(product.id),
                            onAddToCart = {
                                cartViewModel.addToCart(product.id) {
                                    NotificationHelper.showCartNotification(context, product.title)
                                }
                            },
                            onToggleWishlist = { wishlistViewModel.toggleWishlist(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    isWishlisted: Boolean,
    onAddToCart: () -> Unit,
    onToggleWishlist: () -> Unit
) {
    var showSizePicker by remember { mutableStateOf(false) }

    if (showSizePicker) {
        SizePickerSheet(
            productName = product.title,
            onDismiss = { showSizePicker = false },
            onAddToCart = { _ ->
                onAddToCart()
                showSizePicker = false
            }
        )
    }

    Column {
        Box {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFEDE8E3)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onToggleWishlist,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(32.dp)
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
        Text(product.title, fontSize = 11.sp, letterSpacing = 0.5.sp, color = MaiaText, maxLines = 1)
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (product.discountPercent != null) {
                Column {
                    Text(
                        "${String.format("%.0f", product.price)} EUR",
                        fontSize = 10.sp,
                        color = MaiaTextSecondary,
                        textDecoration = TextDecoration.LineThrough
                    )
                    val salePrice = product.price * (1 - product.discountPercent / 100.0)
                    Text("${String.format("%.0f", salePrice)} EUR", fontSize = 11.sp, color = Color(0xFF8B1A1A))
                }
            } else {
                Text("${String.format("%.0f", product.price)} EUR", fontSize = 11.sp, color = MaiaTextSecondary)
            }
            TextButton(
                onClick = { showSizePicker = true },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(20.dp)
            ) {
                Text("+", fontSize = 16.sp, color = MaiaText, fontWeight = FontWeight.Light)
            }
        }
    }
}

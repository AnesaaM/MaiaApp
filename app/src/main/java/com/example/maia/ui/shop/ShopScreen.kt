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
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.example.maia.ui.components.MaiaButton
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

private val shopColorOptions = listOf(
    Color.Black, Color.White, Color(0xFF1565C0), Color(0xFFC62828),
    Color(0xFFE91E63), Color(0xFF757575), Color(0xFF1A237E), Color(0xFFD2B48C),
    Color(0xFF5D4037), Color(0xFF2E7D32), Color(0xFFCDC302)
)

private val shopColorNames = mapOf(
    Color.Black       to "black",
    Color.White       to "white",
    Color(0xFF1565C0) to "blue",
    Color(0xFFC62828) to "red",
    Color(0xFFE91E63) to "pink",
    Color(0xFF757575) to "gray",
    Color(0xFF1A237E) to "navy",
    Color(0xFFD2B48C) to "beige",
    Color(0xFF5D4037) to "brown",
    Color(0xFF2E7D32) to "green",
    Color(0xFFCDC302) to "yellow"
)

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
    var priceAsc by remember { mutableStateOf<Boolean?>(null) }
    var selectedColor by remember { mutableStateOf<Color?>(null) }

    val pagerState = rememberPagerState(initialPage = initialSection, pageCount = { sections.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { wishlistViewModel.loadWishlist() }

    LaunchedEffect(pagerState.currentPage) {
        productVm.switchSection(sections[pagerState.currentPage].second)
        priceAsc = null
        selectedColor = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        BlobHeader(
            leading = {
                IconButton(
                    onClick = {
                        val idx = sections.indexOfFirst { it.second == productVm.currentSection.value }
                        navController.navigate(Screen.Menu.createRoute(idx)) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
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

        // Price filter — all sections
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = if (priceAsc == true) MaiaButton else Color.Transparent,
                modifier = Modifier
                    .border(0.8.dp, if (priceAsc == true) MaiaButton else Color(0xFFCCC0BB), RoundedCornerShape(2.dp))
                    .clickable { priceAsc = if (priceAsc == true) null else true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PRICE", fontSize = 10.sp, letterSpacing = 1.sp, color = if (priceAsc == true) Color.White else MaiaTextSecondary)
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (priceAsc == true) Color.White else MaiaTextSecondary)
                }
            }

            Surface(
                shape = RoundedCornerShape(2.dp),
                color = if (priceAsc == false) MaiaButton else Color.Transparent,
                modifier = Modifier
                    .border(0.8.dp, if (priceAsc == false) MaiaButton else Color(0xFFCCC0BB), RoundedCornerShape(2.dp))
                    .clickable { priceAsc = if (priceAsc == false) null else false }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PRICE", fontSize = 10.sp, letterSpacing = 1.sp, color = if (priceAsc == false) Color.White else MaiaTextSecondary)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (priceAsc == false) Color.White else MaiaTextSecondary)
                }
            }

            if (priceAsc != null || selectedColor != null) {
                Text(
                    "CLEAR ALL",
                    fontSize = 10.sp, letterSpacing = 1.sp,
                    color = MaiaTextSecondary,
                    modifier = Modifier.clickable { priceAsc = null; selectedColor = null }
                )
            }
        }

        // Color filter — WOMAN and MAN only
        if (pagerState.currentPage != 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("COLOR", fontSize = 9.sp, letterSpacing = 2.sp, color = MaiaTextSecondary)
                Spacer(Modifier.width(4.dp))
                shopColorOptions.forEach { color ->
                    val isSelected = selectedColor == color
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 2.dp else 0.8.dp,
                                color = if (isSelected) MaiaText else Color(0xFFCCC0BB),
                                shape = CircleShape
                            )
                            .clickable { selectedColor = if (isSelected) null else color }
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFFEDE8E3), thickness = 0.5.dp)

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

        val baseProducts = when {
            categoryFilter == 0 -> productVm.filteredProducts
            else -> productVm.filteredProducts.filter { it.categoryId == categoryFilter }
        }
        val colorFiltered = if (selectedColor != null) {
            val name = shopColorNames[selectedColor]
            if (name != null) baseProducts.filter { it.color.lowercase() == name } else baseProducts
        } else baseProducts
        val displayProducts = when (priceAsc) {
            true  -> colorFiltered.sortedByDescending { it.price }
            false -> colorFiltered.sortedBy { it.price }
            null  -> colorFiltered
        }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaiaText, strokeWidth = 1.5.dp)
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Failed to load products", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(error, color = MaiaTextSecondary, fontSize = 11.sp)
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
                        val source = when (pagerState.currentPage) { 0 -> "women"; 1 -> "men"; else -> "kids" }
                        ProductCard(
                            product = product,
                            isWishlisted = wishlistViewModel.isWishlisted(product.id),
                            onAddToCart = { size ->
                                cartViewModel.addToCart(
                                    product = product,
                                    productSource = source,
                                    size = size,
                                    onSuccess = { NotificationHelper.showCartNotification(context, product.title) },
                                    onError = { msg -> android.widget.Toast.makeText(context, "Cart error: $msg", android.widget.Toast.LENGTH_LONG).show() }
                                )
                            },
                            onToggleWishlist = { wishlistViewModel.toggleWishlist(product.id, product.title, product.imageUrl, product.price) }
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
    onAddToCart: (String) -> Unit,
    onToggleWishlist: () -> Unit
) {
    var showSizePicker by remember { mutableStateOf(false) }

    if (showSizePicker) {
        SizePickerSheet(
            productName = product.title,
            onDismiss = { showSizePicker = false },
            onAddToCart = { size ->
                onAddToCart(size)
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

package com.example.maia.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.maia.model.Product
import com.example.maia.model.Section
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaButton
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.ui.components.SizePickerSheet
import com.example.maia.util.NotificationHelper
import com.example.maia.viewmodel.CartViewModel
import com.example.maia.viewmodel.ProductViewModel
import com.example.maia.viewmodel.WishlistViewModel

private val colorOptions = listOf(
    Color.Black, Color.White, Color(0xFF1565C0), Color(0xFFC62828),
    Color(0xFFE91E63), Color(0xFF757575), Color(0xFF1A237E), Color(0xFFD2B48C),
    Color(0xFF5D4037), Color(0xFF2E7D32), Color(0xFFCDC302)
)

private val colorNames = mapOf(
    Color.Black        to "black",
    Color.White        to "white",
    Color(0xFF1565C0)  to "blue",
    Color(0xFFC62828)  to "red",
    Color(0xFFE91E63)  to "pink",
    Color(0xFF757575)  to "gray",
    Color(0xFF1A237E)  to "navy",
    Color(0xFFD2B48C)  to "beige",
    Color(0xFF5D4037)  to "brown",
    Color(0xFF2E7D32)  to "green",
    Color(0xFFCDC302)  to "yellow"
)

private val womanCategoryFilter = mapOf(
    "BAGS" to 8, "DRESSES" to 2, "JACKETS" to 4, "SHOES" to 7
)
private val manCategoryFilter = mapOf(
    "SHIRTS" to 1, "TROUSERS" to 2, "SUITS" to 3, "SHOES" to 6
)
private val kidsCategoryKeyword = mapOf(
    "T-SHIRTS" to "shirt", "DRESSES" to "dress", "JEANS" to "jean", "SHOES" to "shoe"
)

private val tabs = listOf("WOMAN" to Section.WOMAN, "MAN" to Section.MAN, "KIDS" to Section.KIDS)
private val womanCategories = listOf("BAGS", "DRESSES", "JACKETS", "SHOES")
private val manCategories   = listOf("SHIRTS", "TROUSERS", "SUITS", "SHOES")
private val kidsCategories  = listOf("T-SHIRTS", "DRESSES", "JEANS", "SHOES")

@Preview(showBackground = true, name = "Search Screen")
@Composable
fun SearchScreenPreview() {
    SearchScreen(
        navController = rememberNavController(),
        cartViewModel = CartViewModel(),
        wishlistViewModel = WishlistViewModel()
    )
}

@Composable
fun SearchScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel
) {
    val productVm: ProductViewModel = viewModel()
    val context = LocalContext.current
    val blobColor = MaiaBlob

    var selectedTab by remember { mutableIntStateOf(0) }
    var showFilters by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf<Color?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var sortMode by remember { mutableStateOf<String?>(null) }
    val sortOptions = listOf("PRICE ↑" to "price_asc", "PRICE ↓" to "price_desc", "A→Z" to "az", "Z→A" to "za", "SALE" to "discount")

    LaunchedEffect(selectedTab) {
        productVm.switchSection(tabs[selectedTab].second)
        selectedCategory = null
    }

    val products = productVm.filteredProducts
    val isLoading = productVm.isLoading.value

    val categoryChips = when (selectedTab) {
        0 -> womanCategories
        1 -> manCategories
        else -> kidsCategories
    }

    val sectionLabel = tabs[selectedTab].first
    val filterCount = listOfNotNull(selectedColor, selectedCategory).size

    Column(modifier = Modifier.fillMaxSize().background(MaiaBackground)) {

        // Header blob
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
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
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    tint = MaiaText,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { navController.navigate(Screen.Menu.createRoute()) }
                )
                Spacer(Modifier.width(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    tabs.forEachIndexed { index, (label, _) ->
                        val selected = selectedTab == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedTab = index }
                        ) {
                            Text(
                                label,
                                fontSize = if (selected) 22.sp else 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Light,
                                color = if (selected) MaiaText else MaiaTextSecondary,
                                letterSpacing = 1.sp
                            )
                            if (selected) {
                                Spacer(Modifier.height(3.dp))
                                Box(Modifier.size(5.dp).clip(CircleShape).background(MaiaText))
                            }
                        }
                    }
                }
                Text(
                    "MAIA", fontSize = 18.sp, fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic, fontWeight = FontWeight.Medium,
                    color = MaiaText, letterSpacing = 2.sp
                )
            }
        }

        // Search bar
        OutlinedTextField(
            value = productVm.searchQuery.value,
            onValueChange = { productVm.updateSearch(it) },
            placeholder = {
                Text(
                    "SEARCH PRODUCTS...",
                    color = Color(0xFFBBABA4),
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaiaTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (productVm.searchQuery.value.isNotEmpty()) {
                    IconButton(onClick = { productVm.updateSearch("") }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaiaTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            shape = RoundedCornerShape(2.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFDDD0CA),
                focusedBorderColor = MaiaText,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                color = MaiaText,
                letterSpacing = 1.sp
            )
        )

        // Filter bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = if (showFilters) MaiaButton else Color.Transparent,
                modifier = Modifier
                    .border(0.8.dp, if (showFilters) MaiaButton else MaiaText, RoundedCornerShape(2.dp))
                    .clickable { showFilters = !showFilters }
            ) {
                Text(
                    if (filterCount > 0) "FILTERS ($filterCount)" else "FILTERS",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    fontSize = 10.sp, letterSpacing = 1.5.sp,
                    color = if (showFilters) Color.White else MaiaText,
                    fontWeight = FontWeight.Medium
                )
            }

            sortOptions.forEach { (label, key) ->
                val selected = sortMode == key
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (selected) MaiaButton else Color.Transparent,
                    modifier = Modifier
                        .border(0.8.dp, if (selected) MaiaButton else Color(0xFFCCC0BB), RoundedCornerShape(2.dp))
                        .clickable { sortMode = if (selected) null else key }
                ) {
                    Text(
                        label,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        fontSize = 10.sp, letterSpacing = 1.sp,
                        color = if (selected) Color.White else MaiaTextSecondary
                    )
                }
            }

            if (filterCount > 0 || sortMode != null) {
                Text(
                    "CLEAR ALL", fontSize = 10.sp, letterSpacing = 1.sp,
                    color = MaiaTextSecondary,
                    modifier = Modifier.clickable {
                        selectedColor = null; selectedCategory = null; sortMode = null
                    }
                )
            }
        }

        // Filter panel
        if (showFilters) {
            HorizontalDivider(color = Color(0xFFEDE8E3), thickness = 0.5.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column {
                    Text("COLOR", fontSize = 9.sp, letterSpacing = 2.sp, color = MaiaTextSecondary)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        colorOptions.forEach { color ->
                            val selected = selectedColor == color
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (selected) 2.dp else 0.dp,
                                        if (selected) MaiaText else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedColor = if (selected) null else color }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CATEGORY", fontSize = 9.sp, letterSpacing = 2.sp, color = MaiaTextSecondary)
                categoryChips.forEach { cat ->
                    val sel = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .border(0.8.dp, if (sel) MaiaText else Color(0xFFCCC0BB), RoundedCornerShape(2.dp))
                            .clickable { selectedCategory = if (sel) null else cat }
                    ) {
                        Text(
                            cat,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 9.sp, letterSpacing = 1.sp,
                            color = if (sel) MaiaText else MaiaTextSecondary,
                            fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFEDE8E3), thickness = 0.5.dp)
        }

        // Apply color + category filters, then sort
        val filtered = products
            .let { list ->
                val colorName = selectedColor?.let { colorNames[it] }
                if (colorName != null) list.filter { it.color.lowercase().contains(colorName) }
                else list
            }
            .let { list ->
                if (selectedCategory != null) {
                    when (selectedTab) {
                        0 -> womanCategoryFilter[selectedCategory]?.let { id -> list.filter { it.categoryId == id } } ?: list
                        1 -> manCategoryFilter[selectedCategory]?.let { id -> list.filter { it.categoryId == id } } ?: list
                        else -> kidsCategoryKeyword[selectedCategory]?.let { kw -> list.filter { it.title.lowercase().contains(kw) } } ?: list
                    }
                } else list
            }

        val sorted = when (sortMode) {
            "price_asc"  -> filtered.sortedBy { it.price }
            "price_desc" -> filtered.sortedByDescending { it.price }
            "az"         -> filtered.sortedBy { it.title }
            "za"         -> filtered.sortedByDescending { it.title }
            "discount"   -> filtered.sortedByDescending { it.discountPercent ?: 0 }
            else         -> filtered
        }

        Text(
            "$sectionLabel · ${sorted.size} ITEMS",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 10.sp, letterSpacing = 2.sp, color = MaiaTextSecondary
        )

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaiaText, strokeWidth = 1.5.dp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sorted) { product ->
                    val source = when (selectedTab) { 0 -> "women"; 1 -> "men"; else -> "kids" }
                    SearchProductCard(
                        product = product,
                        onAddToCart = { size ->
                            cartViewModel.addToCart(
                                product = product,
                                productSource = source,
                                size = size,
                                onSuccess = { NotificationHelper.showCartNotification(context, product.title) },
                                onError = { msg -> android.widget.Toast.makeText(context, "Cart error: $msg", android.widget.Toast.LENGTH_LONG).show() }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchProductCard(product: Product, onAddToCart: (String) -> Unit) {
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

    Column(modifier = Modifier.clickable { showSizePicker = true }) {
        Box {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(Color(0xFFEDE8E3)),
                contentScale = ContentScale.Crop
            )
            if (product.discountPercent != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .background(Color(0xFF8B1A1A), RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("-${product.discountPercent}%", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            product.title.uppercase(),
            fontSize = 10.sp, letterSpacing = 1.sp,
            color = MaiaText, fontWeight = FontWeight.SemiBold, maxLines = 1
        )
        if (product.discountPercent != null) {
            val salePrice = product.price * (1 - product.discountPercent / 100.0)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${String.format("%.0f", product.price)} EUR",
                    fontSize = 9.sp, color = MaiaTextSecondary,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )
                Text("${String.format("%.0f", salePrice)} EUR", fontSize = 10.sp, color = Color(0xFF8B1A1A))
            }
        } else {
            Text(
                "${String.format("%.0f", product.price)} EUR",
                fontSize = 10.sp, color = MaiaTextSecondary
            )
        }
    }
}

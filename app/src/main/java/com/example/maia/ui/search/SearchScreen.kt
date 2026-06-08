package com.example.maia.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
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
    var priceAsc by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(selectedTab) {
        productVm.switchSection(tabs[selectedTab].second)
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
                Icon(Icons.Default.Menu, contentDescription = null, tint = MaiaText, modifier = Modifier.size(22.dp))
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

        // Filter bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FILTERS button
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

            // PRICE ↑
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

            // PRICE ↓
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

            if (filterCount > 0 || priceAsc != null) {
                Text(
                    "CLEAR ALL", fontSize = 10.sp, letterSpacing = 1.sp,
                    color = MaiaTextSecondary,
                    modifier = Modifier.clickable {
                        selectedColor = null; selectedCategory = null; priceAsc = null
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
                // COLOR
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

            // CATEGORY chips
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

        // Results header
        val sorted = when (priceAsc) {
            true  -> products.sortedBy { it.price }
            false -> products.sortedByDescending { it.price }
            null  -> products
        }

        Text(
            "$sectionLabel · ${sorted.size} ITEMS",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 10.sp, letterSpacing = 2.sp, color = MaiaTextSecondary
        )

        // Product grid
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
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.TopStart)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            product.title.uppercase(),
            fontSize = 10.sp, letterSpacing = 1.sp,
            color = MaiaText, fontWeight = FontWeight.SemiBold, maxLines = 1
        )
        Text(
            "${String.format("%.0f", product.price)} EUR",
            fontSize = 10.sp, color = MaiaTextSecondary
        )
    }
}

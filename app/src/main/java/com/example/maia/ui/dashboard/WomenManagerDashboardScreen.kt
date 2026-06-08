package com.example.maia.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.maia.data.TokenManager
import com.example.maia.model.WomenCard
import com.example.maia.model.women.WomenCardRequest
import com.example.maia.model.women.WomenCategory
import com.example.maia.navigation.Screen
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory
import com.example.maia.viewmodel.WomenManagerViewModel

private val WNavItems = listOf(
    DashNavItem("◆", "OVERVIEW"),
    DashNavItem("◇", "PRODUCTS"),
    DashNavItem("○", "CATEGORIES"),
    DashNavItem("◑", "SALES")
)

@Composable
fun WomenManagerDashboardScreen(navController: NavController, tokenManager: TokenManager) {
    val vm: WomenManagerViewModel = viewModel()
    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    var tab by remember { mutableIntStateOf(0) }

    DashboardLayout(
        roleTitle = "WOMEN MANAGER",
        navItems = WNavItems,
        selectedIndex = tab,
        onNavSelect = { tab = it },
        username = tokenManager.getUsername() ?: "Women Manager",
        email = tokenManager.getEmail() ?: "",
        onLogout = {
            authVm.logout()
            navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
        },
        onHomePage = {
            navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } }
        }
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(
                WNavItems[tab].label,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp),
                fontSize = 11.sp, letterSpacing = 3.sp, color = DashSecText
            )
            if (vm.error != null) Text(vm.error!!, color = DashRedText, modifier = Modifier.padding(horizontal = 24.dp), fontSize = 12.sp)
            when (tab) {
                0 -> WOverviewTab(vm)
                1 -> WProductsTab(vm)
                2 -> WCategoriesTab(vm)
                3 -> WSalesTab(vm)
            }
        }
    }
}

// ── Overview ─────────────────────────────────────────────────────────────────

@Composable
private fun WOverviewTab(vm: WomenManagerViewModel) {
    if (vm.isLoading) { WLoadingBox(); return }
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            "TOTAL PRODUCTS" to vm.cards.size.toString(),
            "CATEGORIES"     to vm.categories.size.toString(),
            "ON SALE"        to vm.cards.count { (it.discountPercent ?: 0) > 0 }.toString()
        ).forEach { (label, value) ->
            Card(
                Modifier.width(180.dp),
                colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = BorderStroke(1.dp, DashBorder),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(label, fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                    Text(value, fontSize = 32.sp, fontWeight = FontWeight.Light, color = Color(0xFF1C0A06))
                }
            }
        }
    }
}

// ── Products ──────────────────────────────────────────────────────────────────

@Composable
private fun WProductsTab(vm: WomenManagerViewModel) {
    if (vm.isLoading) { WLoadingBox(); return }
    var search by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<WomenCard?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }
    val list = vm.cards.filter { search.isBlank() || it.title.contains(search, true) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            WSearchBar(search) { search = it }
            WBtnDark("+ ADD PRODUCT") { showAdd = true }
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(list) { p ->
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DashCardBg),
                    border = BorderStroke(1.dp, DashBorder),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        AsyncImage(
                            model = p.imageUrl, contentDescription = null,
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = ColorPainter(Color(0xFFEEE8E2)),
                            error = ColorPainter(Color(0xFFEEE8E2))
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(p.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1C0A06), maxLines = 2)
                            Text("€${"%.2f".format(p.price)} · ${p.category}", fontSize = 12.sp, color = DashSecText)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    WBtnOutlined("EDIT") { editTarget = p }
                                    WBtnDelete { vm.deleteCard(p.id) { e -> actionError = e } }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    if (showAdd) WCardDialog(null, vm.categories, { showAdd = false }) { req ->
        vm.createCard(req) { e -> actionError = e; if (e == null) showAdd = false }
    }
    editTarget?.let { p ->
        WCardDialog(p, vm.categories, { editTarget = null }) { req ->
            vm.updateCard(p.id, req) { e -> actionError = e; if (e == null) editTarget = null }
        }
    }
}

// ── Categories ────────────────────────────────────────────────────────────────

@Composable
private fun WCategoriesTab(vm: WomenManagerViewModel) {
    if (vm.isLoading) { WLoadingBox(); return }
    var showAdd by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<WomenCategory?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("${vm.categories.size} CATEGORIES", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
            WBtnDark("+ ADD CATEGORY") { showAdd = true }
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.categories) { cat ->
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DashCardBg),
                    border = BorderStroke(1.dp, DashBorder),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text(cat.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1C0A06), modifier = Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            WBtnOutlined("EDIT") { editTarget = cat }
                            WBtnDelete { vm.deleteCategory(cat.id) { e -> actionError = e } }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    if (showAdd) WCategoryDialog(null, { showAdd = false }) { name ->
        vm.createCategory(name) { e -> actionError = e; if (e == null) showAdd = false }
    }
    editTarget?.let { cat ->
        WCategoryDialog(cat.name, { editTarget = null }) { name ->
            vm.updateCategory(cat.id, name) { e -> actionError = e; if (e == null) editTarget = null }
        }
    }
}

// ── Sales ─────────────────────────────────────────────────────────────────────

@Composable
private fun WSalesTab(vm: WomenManagerViewModel) {
    if (vm.isLoading) { WLoadingBox(); return }
    var saleTarget by remember { mutableStateOf<WomenCard?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        Text(
            "${vm.cards.count { (it.discountPercent ?: 0) > 0 }} ITEMS ON SALE",
            fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.cards.sortedByDescending { (it.discountPercent ?: 0) > 0 }) { p ->
                val pct = p.discountPercent ?: 0
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DashCardBg),
                    border = BorderStroke(1.dp, DashBorder),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        AsyncImage(
                            model = p.imageUrl, contentDescription = null,
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = ColorPainter(Color(0xFFEEE8E2)),
                            error = ColorPainter(Color(0xFFEEE8E2))
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                                Text(p.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1C0A06), maxLines = 2, modifier = Modifier.weight(1f).padding(end = 8.dp))
                                if (pct > 0) {
                                    Text("$pct% OFF", fontSize = 9.sp, color = DashGreenText,
                                        modifier = Modifier.background(DashGreenBg, RoundedCornerShape(20.dp)).padding(horizontal = 8.dp, vertical = 3.dp))
                                } else {
                                    Text("NO SALE", fontSize = 9.sp, color = DashSecText,
                                        modifier = Modifier.background(Color(0xFFF0EBE5), RoundedCornerShape(20.dp)).padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }
                            Text("€${"%.2f".format(p.price)}", fontSize = 12.sp, color = DashSecText)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    WBtnDark(if (pct > 0) "EDIT SALE" else "SET SALE") { saleTarget = p }
                                    if (pct > 0) WBtnOutlined("REMOVE") { vm.setDiscount(p.id, 0) { e -> actionError = e } }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    saleTarget?.let { p ->
        WSaleDialog(p.title, p.discountPercent ?: 0, { saleTarget = null }) { pct ->
            vm.setDiscount(p.id, pct) { e -> actionError = e; if (e == null) saleTarget = null }
        }
    }
}

// ── Shared UI primitives ──────────────────────────────────────────────────────

@Composable
private fun WLoadingBox() {
    Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }
}

@Composable
private fun WSearchBar(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        placeholder = { Text("Search products...", color = Color(0xFFBBABA4), fontSize = 12.sp) },
        modifier = Modifier.width(200.dp).height(44.dp),
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DashBorder,
            focusedBorderColor = Color(0xFF1C0A06),
            unfocusedContainerColor = DashCardBg,
            focusedContainerColor = DashCardBg
        )
    )
}

@Composable
private fun WBtnDark(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C0A06)),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        modifier = Modifier.height(36.dp)
    ) { Text(label, fontSize = 10.sp, letterSpacing = 1.sp) }
}

@Composable
private fun WBtnOutlined(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFD0C4BC)),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        modifier = Modifier.height(30.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1C0A06))
    ) { Text(label, fontSize = 9.sp, letterSpacing = 1.sp) }
}

@Composable
private fun WBtnDelete(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B2326)),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        modifier = Modifier.height(30.dp)
    ) { Text("DELETE", fontSize = 9.sp, letterSpacing = 1.sp) }
}

// ── Dialogs ───────────────────────────────────────────────────────────────────

@Composable
private fun WCardDialog(card: WomenCard?, categories: List<WomenCategory>, onDismiss: () -> Unit, onConfirm: (WomenCardRequest) -> Unit) {
    var title       by remember { mutableStateOf(card?.title ?: "") }
    var price       by remember { mutableStateOf(card?.price?.toString() ?: "") }
    var imageUrl    by remember { mutableStateOf(card?.imageUrl ?: "") }
    var description by remember { mutableStateOf(card?.description ?: "") }
    var catId       by remember { mutableIntStateOf(card?.womanCategoryId ?: categories.firstOrNull()?.id ?: 0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (card == null) "ADD PRODUCT" else "EDIT PRODUCT", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl, contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(Color(0xFFEEE8E2)),
                        error = ColorPainter(Color(0xFFE0D6CE))
                    )
                }
                OutlinedTextField(title, { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(price, { price = it }, label = { Text("Price (€)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(imageUrl, { imageUrl = it }, label = { Text("Image URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(description, { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                if (categories.isNotEmpty()) {
                    Text("Category", fontSize = 11.sp, color = DashSecText)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        categories.forEach { c ->
                            FilterChip(selected = catId == c.id, onClick = { catId = c.id }, label = { Text(c.name, fontSize = 10.sp) })
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val p = price.toDoubleOrNull() ?: return@TextButton
                onConfirm(WomenCardRequest(title, description, p, imageUrl.ifBlank { null }, catId))
            }) { Text("SAVE") }
        },
        dismissButton = { TextButton(onDismiss) { Text("CANCEL") } }
    )
}

@Composable
private fun WCategoryDialog(existing: String?, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(existing ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "ADD CATEGORY" else "EDIT CATEGORY", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = { OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth()) },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) { Text("SAVE") } },
        dismissButton = { TextButton(onDismiss) { Text("CANCEL") } }
    )
}

@Composable
private fun WSaleDialog(productTitle: String, currentPct: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var pct by remember { mutableStateOf(if (currentPct > 0) currentPct.toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("SET SALE", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(productTitle, fontSize = 13.sp, color = Color(0xFF1C0A06))
                OutlinedTextField(pct, { pct = it }, label = { Text("Discount %") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(pct.toIntOrNull() ?: 0) }) { Text("APPLY") } },
        dismissButton = { TextButton(onDismiss) { Text("CANCEL") } }
    )
}

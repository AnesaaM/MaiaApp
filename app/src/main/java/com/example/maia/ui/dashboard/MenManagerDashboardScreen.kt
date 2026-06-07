package com.example.maia.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.maia.data.TokenManager
import com.example.maia.model.MenCard
import com.example.maia.model.men.MenCardRequest
import com.example.maia.model.men.MenCategory
import com.example.maia.navigation.Screen
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory
import com.example.maia.viewmodel.MenManagerViewModel

private val MenNavItems = listOf(
    DashNavItem("◆", "OVERVIEW"),
    DashNavItem("◇", "PRODUCTS"),
    DashNavItem("○", "CATEGORIES"),
    DashNavItem("◑", "SALES")
)

@Composable
fun MenManagerDashboardScreen(navController: NavController, tokenManager: TokenManager) {
    val vm: MenManagerViewModel = viewModel()
    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    var tab by remember { mutableIntStateOf(0) }

    DashboardLayout(
        roleTitle = "MEN MANAGER",
        navItems = MenNavItems,
        selectedIndex = tab,
        onNavSelect = { tab = it },
        username = tokenManager.getUsername() ?: "Men Manager",
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
                MenNavItems[tab].label,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp),
                fontSize = 11.sp, letterSpacing = 3.sp, color = DashSecText
            )
            if (vm.error != null) Text(vm.error!!, color = DashRedText, modifier = Modifier.padding(horizontal = 24.dp), fontSize = 12.sp)
            when (tab) {
                0 -> MenOverviewTab(vm)
                1 -> MenProductsTab(vm)
                2 -> MenCategoriesTab(vm)
                3 -> MenSalesTab(vm)
            }
        }
    }
}

@Composable
private fun MenOverviewTab(vm: MenManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            "Products" to vm.cards.size.toString(),
            "Categories" to vm.categories.size.toString(),
            "On Sale" to vm.cards.count { (it.discountPercent ?: 0) > 0 }.toString()
        ).forEach { (label, value) ->
            Card(
                Modifier.width(180.dp),
                colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(label.uppercase(), fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                    Text(value, fontSize = 32.sp, fontWeight = FontWeight.Light, color = Color(0xFF1C0A06))
                }
            }
        }
    }
}

@Composable
private fun MenProductsTab(vm: MenManagerViewModel) {
    var showAdd by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<MenCard?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("${vm.cards.size} PRODUCTS", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                TextButton(onClick = { showAdd = true }) { Text("+ ADD", fontSize = 9.sp, color = Color(0xFF1C0A06)) }
            }
            if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp))
        }
        items(vm.cards) { p ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder), shape = RoundedCornerShape(4.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text(p.title, fontSize = 14.sp, color = Color(0xFF1C0A06), modifier = Modifier.weight(1f))
                        Text("€${"%.2f".format(p.price)}", fontSize = 13.sp, color = DashSecText)
                    }
                    if (p.menCategoryName.isNotEmpty()) Text(p.menCategoryName, fontSize = 11.sp, color = DashSecText)
                    if ((p.discountPercent ?: 0) > 0) Text("${p.discountPercent}% OFF", fontSize = 10.sp, color = DashGreenText,
                        modifier = Modifier.background(DashGreenBg, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { editTarget = p }, contentPadding = PaddingValues(0.dp)) { Text("EDIT", fontSize = 9.sp, color = Color(0xFF1C0A06)) }
                        TextButton(onClick = { vm.deleteCard(p.id) { e -> actionError = e } }, contentPadding = PaddingValues(0.dp)) { Text("DELETE", fontSize = 9.sp, color = DashRedText) }
                    }
                }
            }
        }
    }

    if (showAdd) MenCardDialog(null, vm.categories, onDismiss = { showAdd = false },
        onConfirm = { req -> vm.createCard(req) { e -> actionError = e; if (e == null) showAdd = false } })

    editTarget?.let { p ->
        MenCardDialog(p, vm.categories, onDismiss = { editTarget = null },
            onConfirm = { req -> vm.updateCard(p.id, req) { e -> actionError = e; if (e == null) editTarget = null } })
    }
}

@Composable
private fun MenCategoriesTab(vm: MenManagerViewModel) {
    var showAdd by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<MenCategory?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("${vm.categories.size} CATEGORIES", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                TextButton(onClick = { showAdd = true }) { Text("+ ADD", fontSize = 9.sp, color = Color(0xFF1C0A06)) }
            }
            if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp))
        }
        items(vm.categories) { cat ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder), shape = RoundedCornerShape(4.dp)) {
                Row(Modifier.padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(cat.name, fontSize = 14.sp, color = Color(0xFF1C0A06))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { editTarget = cat }, contentPadding = PaddingValues(0.dp)) { Text("EDIT", fontSize = 9.sp, color = Color(0xFF1C0A06)) }
                        TextButton(onClick = { vm.deleteCategory(cat.id) { e -> actionError = e } }, contentPadding = PaddingValues(0.dp)) { Text("DELETE", fontSize = 9.sp, color = DashRedText) }
                    }
                }
            }
        }
    }

    if (showAdd) MenCategoryDialog(null, onDismiss = { showAdd = false },
        onConfirm = { name -> vm.createCategory(name) { e -> actionError = e; if (e == null) showAdd = false } })

    editTarget?.let { cat ->
        MenCategoryDialog(cat.name, onDismiss = { editTarget = null },
            onConfirm = { name -> vm.updateCategory(cat.id, name) { e -> actionError = e; if (e == null) editTarget = null } })
    }
}

@Composable
private fun MenSalesTab(vm: MenManagerViewModel) {
    var discountTarget by remember { mutableStateOf<MenCard?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("${vm.cards.count { (it.discountPercent ?: 0) > 0 }} ITEMS ON SALE", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
            if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp))
        }
        items(vm.cards) { p ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder), shape = RoundedCornerShape(4.dp)) {
                Row(Modifier.padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(p.title, fontSize = 13.sp, color = Color(0xFF1C0A06))
                        Text("€${"%.2f".format(p.price)}", fontSize = 11.sp, color = DashSecText)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        if ((p.discountPercent ?: 0) > 0) Text("${p.discountPercent}% OFF", fontSize = 10.sp, color = DashGreenText,
                            modifier = Modifier.background(DashGreenBg, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp))
                        TextButton(onClick = { discountTarget = p }, contentPadding = PaddingValues(0.dp)) {
                            Text(if ((p.discountPercent ?: 0) > 0) "EDIT" else "SET SALE", fontSize = 9.sp, color = Color(0xFF1C0A06))
                        }
                        if ((p.discountPercent ?: 0) > 0) TextButton(onClick = { vm.setDiscount(p, 0) { e -> actionError = e } }, contentPadding = PaddingValues(0.dp)) {
                            Text("REMOVE", fontSize = 9.sp, color = DashSecText)
                        }
                    }
                }
            }
        }
    }

    discountTarget?.let { p ->
        MenDiscountDialog(p.title, p.discountPercent ?: 0, onDismiss = { discountTarget = null },
            onConfirm = { pct -> vm.setDiscount(p, pct) { e -> actionError = e; if (e == null) discountTarget = null } })
    }
}

@Composable
private fun MenCardDialog(card: MenCard?, categories: List<MenCategory>, onDismiss: () -> Unit, onConfirm: (MenCardRequest) -> Unit) {
    var title by remember { mutableStateOf(card?.title ?: "") }
    var price by remember { mutableStateOf(card?.price?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(card?.imageUrl ?: "") }
    var description by remember { mutableStateOf(card?.description ?: "") }
    var catId by remember { mutableIntStateOf(card?.menCategoryId ?: categories.firstOrNull()?.id ?: 0) }

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(if (card == null) "ADD PRODUCT" else "EDIT PRODUCT", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true)
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (€)") }, singleLine = true)
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                if (categories.isNotEmpty()) {
                    Text("Category", fontSize = 11.sp, color = DashSecText)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        categories.forEach { cat -> FilterChip(selected = catId == cat.id, onClick = { catId = cat.id }, label = { Text(cat.name, fontSize = 10.sp) }) }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { val p = price.toDoubleOrNull() ?: return@TextButton; onConfirm(MenCardRequest(title, description, p, imageUrl.ifBlank { null }, catId)) }) { Text("SAVE") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } })
}

@Composable
private fun MenCategoryDialog(existing: String?, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(existing ?: "") }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "ADD CATEGORY" else "EDIT CATEGORY", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true) },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) { Text("SAVE") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } })
}

@Composable
private fun MenDiscountDialog(productTitle: String, currentPct: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var pct by remember { mutableStateOf(currentPct.toString()) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("SET SALE", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(productTitle, fontSize = 13.sp, color = Color(0xFF1C0A06))
                OutlinedTextField(value = pct, onValueChange = { pct = it }, label = { Text("Discount %") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(pct.toIntOrNull() ?: 0) }) { Text("APPLY") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } })
}

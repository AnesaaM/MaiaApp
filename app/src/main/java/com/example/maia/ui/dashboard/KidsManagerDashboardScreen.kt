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
import com.example.maia.model.KidsCards
import com.example.maia.navigation.Screen
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory
import com.example.maia.viewmodel.KidsManagerViewModel

private val KidsNavItems = listOf(
    DashNavItem("◆", "OVERVIEW"),
    DashNavItem("◇", "PRODUCTS"),
    DashNavItem("○", "CATEGORIES"),
    DashNavItem("○", "TYPES"),
    DashNavItem("◑", "SALES")
)

@Composable
fun KidsManagerDashboardScreen(navController: NavController, tokenManager: TokenManager) {
    val vm: KidsManagerViewModel = viewModel()
    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    var tab by remember { mutableIntStateOf(0) }

    DashboardLayout(
        roleTitle = "KIDS MANAGER",
        navItems = KidsNavItems,
        selectedIndex = tab,
        onNavSelect = { tab = it },
        username = tokenManager.getUsername() ?: "Kids Manager",
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
                KidsNavItems[tab].label,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp),
                fontSize = 11.sp, letterSpacing = 3.sp, color = DashSecText
            )
            if (vm.error != null) Text(vm.error!!, color = DashRedText, modifier = Modifier.padding(horizontal = 24.dp), fontSize = 12.sp)
            when (tab) {
                0 -> KidsOverviewTab(vm)
                1 -> KidsProductsTab(vm)
                2 -> KidsSimpleListTab(vm, "categories")
                3 -> KidsSimpleListTab(vm, "types")
                4 -> KidsSalesTab(vm)
            }
        }
    }
}

@Composable
private fun KidsOverviewTab(vm: KidsManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            "Products" to vm.cards.size.toString(),
            "Categories" to vm.categories.size.toString(),
            "Types" to vm.productTypes.size.toString(),
            "On Sale" to vm.cards.count { (it.discountPercent ?: 0) > 0 }.toString()
        ).forEach { (label, value) ->
            Card(
                Modifier.width(160.dp),
                colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(label.uppercase(), fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                    Text(value, fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color(0xFF1C0A06))
                }
            }
        }
    }
}

@Composable
private fun KidsProductsTab(vm: KidsManagerViewModel) {
    var actionError by remember { mutableStateOf<String?>(null) }

    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("${vm.cards.size} PRODUCTS", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
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
                    if ((p.discountPercent ?: 0) > 0) Text("${p.discountPercent}% OFF", fontSize = 10.sp, color = DashGreenText,
                        modifier = Modifier.background(DashGreenBg, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp))
                    TextButton(onClick = { vm.deleteCard(p.id) { e -> actionError = e } }, contentPadding = PaddingValues(0.dp)) {
                        Text("DELETE", fontSize = 9.sp, color = DashRedText)
                    }
                }
            }
        }
    }
}

@Composable
private fun KidsSimpleListTab(vm: KidsManagerViewModel, kind: String) {
    var showAdd by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    val items: List<Pair<Int, String>> = if (kind == "categories")
        vm.categories.map { it.id to it.name }
    else
        vm.productTypes.map { it.id to it.name }

    val label = if (kind == "categories") "CATEGORIES" else "PRODUCT TYPES"

    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("${items.size} $label", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                TextButton(onClick = { showAdd = true }) { Text("+ ADD", fontSize = 9.sp, color = Color(0xFF1C0A06)) }
            }
            if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp))
        }
        items(items) { (id, name) ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder), shape = RoundedCornerShape(4.dp)) {
                Row(Modifier.padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(name, fontSize = 14.sp, color = Color(0xFF1C0A06))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { editTarget = id to name }, contentPadding = PaddingValues(0.dp)) { Text("EDIT", fontSize = 9.sp, color = Color(0xFF1C0A06)) }
                        TextButton(onClick = {
                            if (kind == "categories") vm.deleteCategory(id) { e -> actionError = e }
                            else vm.deleteProductType(id) { e -> actionError = e }
                        }, contentPadding = PaddingValues(0.dp)) { Text("DELETE", fontSize = 9.sp, color = DashRedText) }
                    }
                }
            }
        }
    }

    if (showAdd) KidsNameDialog(null, onDismiss = { showAdd = false },
        onConfirm = { name ->
            if (kind == "categories") vm.createCategory(name) { e -> actionError = e; if (e == null) showAdd = false }
            else vm.createProductType(name) { e -> actionError = e; if (e == null) showAdd = false }
        })

    editTarget?.let { (id, existingName) ->
        KidsNameDialog(existingName, onDismiss = { editTarget = null },
            onConfirm = { name ->
                if (kind == "categories") vm.updateCategory(id, name) { e -> actionError = e; if (e == null) editTarget = null }
                else vm.updateProductType(id, name) { e -> actionError = e; if (e == null) editTarget = null }
            })
    }
}

@Composable
private fun KidsSalesTab(vm: KidsManagerViewModel) {
    var discountTarget by remember { mutableStateOf<KidsCards?>(null) }
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
                        if ((p.discountPercent ?: 0) > 0) TextButton(onClick = { vm.setDiscount(p.id, 0) { e -> actionError = e } }, contentPadding = PaddingValues(0.dp)) {
                            Text("REMOVE", fontSize = 9.sp, color = DashSecText)
                        }
                    }
                }
            }
        }
    }

    discountTarget?.let { p ->
        KidsDiscountDialog(p.title, p.discountPercent ?: 0, onDismiss = { discountTarget = null },
            onConfirm = { pct -> vm.setDiscount(p.id, pct) { e -> actionError = e; if (e == null) discountTarget = null } })
    }
}

@Composable
private fun KidsNameDialog(existing: String?, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(existing ?: "") }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "ADD" else "EDIT", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true) },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) { Text("SAVE") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } })
}

@Composable
private fun KidsDiscountDialog(title: String, currentPct: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var pct by remember { mutableStateOf(currentPct.toString()) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("SET SALE", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(title, fontSize = 13.sp, color = Color(0xFF1C0A06))
                OutlinedTextField(value = pct, onValueChange = { pct = it }, label = { Text("Discount %") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(pct.toIntOrNull() ?: 0) }) { Text("APPLY") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } })
}

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
import com.example.maia.model.WomenCard
import com.example.maia.model.admin.User
import com.example.maia.model.order.Order
import com.example.maia.navigation.Screen
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory
import com.example.maia.viewmodel.SalesManagerViewModel

private val SalesNavItems = listOf(
    DashNavItem("◆", "OVERVIEW"),
    DashNavItem("○", "CUSTOMERS"),
    DashNavItem("◇", "WOMEN"),
    DashNavItem("◆", "MEN"),
    DashNavItem("◇", "KIDS"),
    DashNavItem("◑", "ACTIVE SALES"),
    DashNavItem("▣", "ORDERS")
)

@Composable
fun SalesManagerDashboardScreen(navController: NavController, tokenManager: TokenManager) {
    val vm: SalesManagerViewModel = viewModel()
    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    var tab by remember { mutableIntStateOf(0) }

    DashboardLayout(
        roleTitle = "SALES MANAGER",
        navItems = SalesNavItems,
        selectedIndex = tab,
        onNavSelect = { tab = it },
        username = tokenManager.getUsername() ?: "Sales Manager",
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
                SalesNavItems[tab].label,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp),
                fontSize = 11.sp, letterSpacing = 3.sp, color = DashSecText
            )
            if (vm.error != null) Text(vm.error!!, color = DashRedText, modifier = Modifier.padding(horizontal = 24.dp), fontSize = 12.sp)
            when (tab) {
                0 -> SalesOverviewTab(vm)
                1 -> SalesCustomersTab(vm)
                2 -> SalesSectionTab(vm, "women")
                3 -> SalesSectionTab(vm, "men")
                4 -> SalesSectionTab(vm, "kids")
                5 -> ActiveSalesTab(vm)
                6 -> SalesOrdersTab(vm)
            }
        }
    }
}

@Composable
private fun SalesOverviewTab(vm: SalesManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    val stats = listOf(
        "Women Products" to vm.womenCards.size.toString(),
        "Men Products" to vm.menCards.size.toString(),
        "Kids Products" to vm.kidsCards.size.toString(),
        "Items on Sale" to vm.allOnSale.size.toString(),
        "Customers" to vm.customers.size.toString(),
        "Total Orders" to vm.orders.size.toString()
    )
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { (label, value) ->
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
private fun SalesCustomersTab(vm: SalesManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("${vm.customers.size} CUSTOMERS", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText) }
        items(vm.customers) { u ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder), shape = RoundedCornerShape(4.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${u.firstName} ${u.lastName}", fontSize = 14.sp, color = Color(0xFF1C0A06))
                    Text(u.email, fontSize = 11.sp, color = DashSecText)
                }
            }
        }
    }
}

@Composable
private fun SalesSectionTab(vm: SalesManagerViewModel, section: String) {
    var discountTarget by remember { mutableStateOf<Triple<Int, String, Double>?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    val items: List<Triple<Int, String, Double>> = when (section) {
        "women" -> vm.womenCards.map { Triple(it.id, it.title, it.price) }
        "men"   -> vm.menCards.map { Triple(it.id, it.title, it.price) }
        else    -> vm.kidsCards.map { Triple(it.id, it.title, it.price) }
    }
    val discounts: Map<Int, Int> = when (section) {
        "women" -> vm.womenCards.associate { it.id to (it.discountPercent ?: 0) }
        "men"   -> vm.menCards.associate { it.id to (it.discountPercent ?: 0) }
        else    -> vm.kidsCards.associate { it.id to (it.discountPercent ?: 0) }
    }

    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("${items.size} ${section.uppercase()} PRODUCTS", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
            if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp))
        }
        items(items) { (id, title, price) ->
            val pct = discounts[id] ?: 0
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder), shape = RoundedCornerShape(4.dp)) {
                Row(Modifier.padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(title, fontSize = 13.sp, color = Color(0xFF1C0A06))
                        Text("€${"%.2f".format(price)}", fontSize = 11.sp, color = DashSecText)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (pct > 0) Text("$pct% OFF", fontSize = 10.sp, color = DashGreenText,
                            modifier = Modifier.background(DashGreenBg, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp))
                        TextButton(onClick = { discountTarget = Triple(id, title, price) }, contentPadding = PaddingValues(0.dp)) {
                            Text(if (pct > 0) "EDIT" else "SET SALE", fontSize = 9.sp, color = Color(0xFF1C0A06))
                        }
                        if (pct > 0) TextButton(onClick = {
                            when (section) {
                                "women" -> { val c = vm.womenCards.first { it.id == id }; vm.setWomenDiscount(c.id, 0) { e -> actionError = e } }
                                "men"   -> { val c = vm.menCards.first { it.id == id }; vm.setMenDiscount(c, 0) { e -> actionError = e } }
                                else    -> vm.setKidsDiscount(id, 0) { e -> actionError = e }
                            }
                        }, contentPadding = PaddingValues(0.dp)) { Text("REMOVE", fontSize = 9.sp, color = DashSecText) }
                    }
                }
            }
        }
    }

    discountTarget?.let { (id, title, _) ->
        val currentPct = discounts[id] ?: 0
        SalesDiscountDialog(title, currentPct, onDismiss = { discountTarget = null },
            onConfirm = { pct ->
                when (section) {
                    "women" -> { val c = vm.womenCards.first { it.id == id }; vm.setWomenDiscount(c.id, pct) { e -> actionError = e; if (e == null) discountTarget = null } }
                    "men"   -> { val c = vm.menCards.first { it.id == id }; vm.setMenDiscount(c, pct) { e -> actionError = e; if (e == null) discountTarget = null } }
                    else    -> vm.setKidsDiscount(id, pct) { e -> actionError = e; if (e == null) discountTarget = null }
                }
            })
    }
}

@Composable
private fun ActiveSalesTab(vm: SalesManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("${vm.allOnSale.size} ACTIVE SALES", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText) }
        if (vm.allOnSale.isEmpty()) {
            item { Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) { Text("No active sales at the moment.", color = DashSecText, fontSize = 12.sp) } }
        }
        items(vm.allOnSale) { saleItem ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder), shape = RoundedCornerShape(4.dp)) {
                Row(Modifier.padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(saleItem.title, fontSize = 13.sp, color = Color(0xFF1C0A06), modifier = Modifier.weight(1f))
                    Text(saleItem.section, fontSize = 10.sp, color = DashSecText,
                        modifier = Modifier.background(Color(0xFFF0ECE8), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun SalesOrdersTab(vm: SalesManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }

    var statusTarget by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    val statusOptions = listOf("Pending", "Processing", "Shipped", "Delivered", "Cancelled")

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("${vm.orders.size} ORDERS", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
            if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp))
        }
        if (vm.orders.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) {
                    Text("No orders yet.", color = DashSecText, fontSize = 12.sp)
                }
            }
        }
        items(vm.orders, key = { it.id }) { order ->
            val statusColor = when (order.status.lowercase()) {
                "delivered" -> DashGreenText
                "cancelled" -> DashRedText
                "shipped"   -> Color(0xFF1565C0)
                "processing"-> Color(0xFFE65100)
                else        -> DashSecText
            }
            val statusBg = when (order.status.lowercase()) {
                "delivered" -> DashGreenBg
                "cancelled" -> Color(0xFFFFEBEB)
                "shipped"   -> Color(0xFFE3F0FF)
                "processing"-> Color(0xFFFFF3E0)
                else        -> Color(0xFFF0ECE8)
            }
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashBorder),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text(
                            "MAIA-${order.id.toString().padStart(6, '0')}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1C0A06)
                        )
                        Text(
                            order.status.uppercase(),
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = statusColor,
                            modifier = Modifier
                                .background(statusBg, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text(
                            order.createdAt.take(10),
                            fontSize = 11.sp,
                            color = DashSecText
                        )
                        Text(
                            "€${"%.2f".format(order.totalAmount)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1C0A06)
                        )
                    }
                    if (!order.items.isNullOrEmpty()) {
                        Text(
                            "${order.items.size} item${if (order.items.size != 1) "s" else ""}",
                            fontSize = 10.sp,
                            color = DashSecText
                        )
                    }
                    TextButton(
                        onClick = { statusTarget = order.id to order.status },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("UPDATE STATUS", fontSize = 9.sp, letterSpacing = 1.sp, color = Color(0xFF1C0A06))
                    }
                }
            }
        }
    }

    statusTarget?.let { (orderId, currentStatus) ->
        AlertDialog(
            onDismissRequest = { statusTarget = null },
            title = { Text("UPDATE STATUS", fontSize = 11.sp, letterSpacing = 2.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("MAIA-${orderId.toString().padStart(6, '0')}", fontSize = 12.sp, color = DashSecText)
                    Spacer(Modifier.height(8.dp))
                    statusOptions.forEach { status ->
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentStatus.equals(status, ignoreCase = true),
                                onClick = { statusTarget = orderId to status }
                            )
                            Text(status, fontSize = 13.sp, color = Color(0xFF1C0A06))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.updateOrderStatus(orderId, currentStatus) { err ->
                        actionError = err
                        if (err == null) statusTarget = null
                    }
                }) { Text("APPLY") }
            },
            dismissButton = { TextButton(onClick = { statusTarget = null }) { Text("CANCEL") } }
        )
    }
}

@Composable
private fun SalesDiscountDialog(title: String, currentPct: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
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

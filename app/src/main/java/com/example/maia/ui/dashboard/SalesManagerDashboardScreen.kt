package com.example.maia.ui.dashboard

import androidx.compose.foundation.BorderStroke
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
import com.example.maia.model.KidsCards
import com.example.maia.model.MenCard
import com.example.maia.model.WomenCard
import com.example.maia.navigation.Screen
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory
import com.example.maia.viewmodel.SalesManagerViewModel

private val SalesNavItems = listOf(
    DashNavItem("◆", "OVERVIEW"),
    DashNavItem("◇", "WOMEN SALES"),
    DashNavItem("◆", "MEN SALES"),
    DashNavItem("○", "KIDS SALES"),
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
                1 -> SalesWomenTab(vm)
                2 -> SalesMenTab(vm)
                3 -> SalesKidsTab(vm)
                4 -> ActiveSalesTab(vm)
                5 -> SalesOrdersTab(vm)
            }
        }
    }
}

@Composable
private fun SalesOverviewTab(vm: SalesManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    val stats = listOf(
        "Women Products" to vm.womenCards.size.toString(),
        "Men Products"   to vm.menCards.size.toString(),
        "Kids Products"  to vm.kidsCards.size.toString(),
        "Items on Sale"  to vm.allOnSale.size.toString(),
        "Total Orders"   to vm.orders.size.toString()
    )
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { (label, value) ->
            Card(
                Modifier.width(180.dp),
                colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = BorderStroke(1.dp, DashBorder),
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
private fun SalesWomenTab(vm: SalesManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    var saleTarget by remember { mutableStateOf<WomenCard?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        Text(
            "${vm.womenCards.count { (it.discountPercent ?: 0) > 0 }} ITEMS ON SALE",
            fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.womenCards.sortedByDescending { (it.discountPercent ?: 0) > 0 }) { p ->
                SaleProductCard(
                    imageUrl = p.imageUrl,
                    title = p.title,
                    price = p.price,
                    discountPct = p.discountPercent ?: 0,
                    onEditSale = { saleTarget = p },
                    onRemove = { vm.setWomenDiscount(p.id, 0) { e -> actionError = e } }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    saleTarget?.let { p ->
        SalesDiscountDialog(p.title, p.price, p.discountPercent ?: 0, { saleTarget = null }) { pct ->
            vm.setWomenDiscount(p.id, pct) { e -> actionError = e; if (e == null) saleTarget = null }
        }
    }
}

@Composable
private fun SalesMenTab(vm: SalesManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    var saleTarget by remember { mutableStateOf<MenCard?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        Text(
            "${vm.menCards.count { (it.discountPercent ?: 0) > 0 }} ITEMS ON SALE",
            fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.menCards.sortedByDescending { (it.discountPercent ?: 0) > 0 }) { p ->
                SaleProductCard(
                    imageUrl = p.imageUrl,
                    title = p.title,
                    price = p.price,
                    discountPct = p.discountPercent ?: 0,
                    onEditSale = { saleTarget = p },
                    onRemove = { vm.setMenDiscount(p, 0) { e -> actionError = e } }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    saleTarget?.let { p ->
        SalesDiscountDialog(p.title, p.price, p.discountPercent ?: 0, { saleTarget = null }) { pct ->
            vm.setMenDiscount(p, pct) { e -> actionError = e; if (e == null) saleTarget = null }
        }
    }
}

@Composable
private fun SalesKidsTab(vm: SalesManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    var saleTarget by remember { mutableStateOf<KidsCards?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        Text(
            "${vm.kidsCards.count { (it.discountPercent ?: 0) > 0 }} ITEMS ON SALE",
            fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.kidsCards.sortedByDescending { (it.discountPercent ?: 0) > 0 }) { p ->
                SaleProductCard(
                    imageUrl = p.imageUrl,
                    title = p.title,
                    price = p.price,
                    discountPct = p.discountPercent ?: 0,
                    onEditSale = { saleTarget = p },
                    onRemove = { vm.setKidsDiscount(p.id, 0) { e -> actionError = e } }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    saleTarget?.let { p ->
        SalesDiscountDialog(p.title, p.price, p.discountPercent ?: 0, { saleTarget = null }) { pct ->
            vm.setKidsDiscount(p.id, pct) { e -> actionError = e; if (e == null) saleTarget = null }
        }
    }
}

@Composable
private fun SaleProductCard(
    imageUrl: String?,
    title: String,
    price: Double,
    discountPct: Int,
    onEditSale: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DashCardBg),
        border = BorderStroke(1.dp, DashBorder),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color(0xFFEEE8E2)),
                error = ColorPainter(Color(0xFFEEE8E2))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                    Text(
                        title, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        color = Color(0xFF1C0A06), maxLines = 2,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    if (discountPct > 0) {
                        Text(
                            "$discountPct% OFF", fontSize = 9.sp, color = DashGreenText,
                            modifier = Modifier.background(DashGreenBg, RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    } else {
                        Text(
                            "NO SALE", fontSize = 9.sp, color = DashSecText,
                            modifier = Modifier.background(Color(0xFFF0EBE5), RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Text("€${"%.2f".format(price)}", fontSize = 12.sp, color = DashSecText)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SalesBtnDark(if (discountPct > 0) "EDIT SALE" else "SET SALE") { onEditSale() }
                        if (discountPct > 0) SalesBtnOutlined("REMOVE") { onRemove() }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveSalesTab(vm: SalesManagerViewModel) {
    if (vm.isLoading) { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = DashSecText) }; return }
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Text(
            "${vm.allOnSale.size} ACTIVE SALES",
            fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        if (vm.allOnSale.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) {
                Text("No active sales at the moment.", color = DashSecText, fontSize = 12.sp)
            }
        } else {
            LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.allOnSale) { item ->
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DashCardBg),
                        border = BorderStroke(1.dp, DashBorder),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop,
                                placeholder = ColorPainter(Color(0xFFEEE8E2)),
                                error = ColorPainter(Color(0xFFEEE8E2))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Text(
                                        item.title, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1C0A06),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                                    )
                                    Text(
                                        item.section, fontSize = 9.sp, color = DashSecText,
                                        modifier = Modifier.background(Color(0xFFF0EBE5), RoundedCornerShape(20.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Text("€${"%.2f".format(item.price)}", fontSize = 12.sp, color = DashSecText)
                                    Text(
                                        "${item.discountPercent}% OFF", fontSize = 9.sp, color = DashGreenText,
                                        modifier = Modifier.background(DashGreenBg, RoundedCornerShape(20.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
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
private fun SalesDiscountDialog(
    title: String,
    price: Double,
    currentPct: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var pct by remember { mutableStateOf(if (currentPct > 0) currentPct.toString() else "") }
    val pctValue = pct.toIntOrNull() ?: 0
    val salePrice = if (pctValue in 1..99) price * (1 - pctValue / 100.0) else null

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text("APPLY SALE PRICE", fontSize = 11.sp, letterSpacing = 2.sp, color = Color(0xFF1C0A06))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("PRODUCT", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                    OutlinedTextField(
                        value = title, onValueChange = {}, readOnly = true, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = DashBorder,
                            focusedBorderColor = DashBorder,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("DISCOUNT PERCENTAGE", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                    OutlinedTextField(
                        value = pct, onValueChange = { pct = it },
                        placeholder = { Text("e.g. 20", color = Color(0xFFBBABA4), fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = DashBorder,
                            focusedBorderColor = Color(0xFF1C0A06),
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                }
                if (salePrice != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("SALE PRICE PREVIEW", fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                        OutlinedTextField(
                            value = "€${"%.2f".format(salePrice)}", onValueChange = {},
                            readOnly = true, singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = DashBorder,
                                focusedBorderColor = DashBorder,
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(pct.toIntOrNull() ?: 0) },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C0A06))
            ) { Text("APPLY SALE", fontSize = 10.sp, letterSpacing = 1.sp) }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFFD0C4BC)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1C0A06))
            ) { Text("CANCEL", fontSize = 10.sp, letterSpacing = 1.sp) }
        }
    )
}

@Composable
private fun SalesBtnDark(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C0A06)),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        modifier = Modifier.height(30.dp)
    ) { Text(label, fontSize = 9.sp, letterSpacing = 1.sp) }
}

@Composable
private fun SalesBtnOutlined(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFD0C4BC)),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        modifier = Modifier.height(30.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1C0A06))
    ) { Text(label, fontSize = 9.sp, letterSpacing = 1.sp) }
}

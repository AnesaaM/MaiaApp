@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.example.maia.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.maia.model.KidsCards
import com.example.maia.model.MenCard
import com.example.maia.model.WomenCard
import com.example.maia.model.admin.CreateStaffRequest
import com.example.maia.model.admin.UpdateUserRequest
import com.example.maia.model.admin.User
import com.example.maia.model.men.MenCardRequest
import com.example.maia.model.men.MenCategory
import com.example.maia.model.women.WomenCardRequest
import com.example.maia.model.women.WomenCategory
import com.example.maia.navigation.Screen
import com.example.maia.viewmodel.AdminViewModel
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory

// ─── Local colors ────────────────────────────────────────────────────────────
private val TableHeaderBg = Color(0xFFF0EBE5)
private val ActionDark    = Color(0xFF1C0A06)
private val ActionRed     = Color(0xFF9B2326)
private val RowDivider    = Color(0xFFEDE8E2)
private val ChipActiveBg  = Color(0xFFD8EFE3)
private val ChipActiveText= Color(0xFF2E7D52)
private val ChipInactiveBg= Color(0xFFF5D8D8)

private val AdminNavItems = listOf(
    DashNavItem("◆", "OVERVIEW"),
    DashNavItem("○", "CUSTOMERS"),
    DashNavItem("○", "STAFF"),
    DashNavItem("◇", "WOMEN SECTION"),
    DashNavItem("◆", "MEN SECTION"),
    DashNavItem("◇", "KIDS SECTION"),
    DashNavItem("◑", "SALES")
)

private fun roleBg(r: String) = when (r) {
    "SalesManager"  -> Color(0xFFDDE4F5)
    "WomenManager"  -> Color(0xFFD8EFE3)
    "MenManager"    -> Color(0xFFD4E9F5)
    "KidsManager"   -> Color(0xFFF5E8D4)
    else            -> Color(0xFFEEEEEE)
}

private fun roleText(r: String) = when (r) {
    "SalesManager"  -> Color(0xFF3A5CA8)
    "WomenManager"  -> Color(0xFF2E7D52)
    "MenManager"    -> Color(0xFF1A6B9A)
    "KidsManager"   -> Color(0xFF9A6B1A)
    else            -> Color(0xFF555555)
}

private fun fmtDate(raw: String?): String {
    if (raw == null) return "-"
    return try {
        val d = raw.substring(0, 10).split("-")
        "${d[1].trimStart('0')}/${d[2].trimStart('0')}/${d[0]}"
    } catch (_: Exception) { raw.take(10) }
}

// ─── Screen entry point ───────────────────────────────────────────────────────
@Composable
fun AdminDashboardScreen(navController: NavController, tokenManager: TokenManager) {
    val vm: AdminViewModel = viewModel()
    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    var tab by remember { mutableIntStateOf(0) }

    DashboardLayout(
        roleTitle = "ADMIN",
        navItems = AdminNavItems,
        selectedIndex = tab,
        onNavSelect = { tab = it },
        username = tokenManager.getUsername() ?: "Admin",
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
                AdminNavItems[tab].label,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 4.dp),
                fontSize = 11.sp, letterSpacing = 3.sp, color = DashSecText
            )
            if (vm.error != null) {
                Text(
                    vm.error!!,
                    color = DashRedText,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    fontSize = 11.sp
                )
            }
            when (tab) {
                0 -> AdminOverviewTab(vm)
                1 -> AdminCustomersTab(vm)
                2 -> AdminStaffTab(vm)
                3 -> AdminProductsTab(vm, "women")
                4 -> AdminProductsTab(vm, "men")
                5 -> AdminProductsTab(vm, "kids")
                6 -> AdminSalesTab(vm)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// OVERVIEW
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AdminOverviewTab(vm: AdminViewModel) {
    if (vm.isLoading) { LoadingBox(); return }
    val stats = listOf(
        "Customers"      to vm.customers.size,
        "Staff Members"  to vm.staff.size,
        "Women Products" to vm.womenCards.size,
        "Men Products"   to vm.menCards.size,
        "Kids Products"  to vm.kidsCards.size
    )
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { (label, value) ->
            Card(
                Modifier.width(190.dp),
                colors = CardDefaults.cardColors(containerColor = DashCardBg),
                border = BorderStroke(1.dp, DashBorder),
                shape = RoundedCornerShape(6.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(label.uppercase(), fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
                    Text(value.toString(), fontSize = 34.sp, fontWeight = FontWeight.Light, color = ActionDark)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// CUSTOMERS
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AdminCustomersTab(vm: AdminViewModel) {
    if (vm.isLoading) { LoadingBox(); return }
    var search by remember { mutableStateOf("") }
    var actionError by remember { mutableStateOf<String?>(null) }
    val filtered = vm.customers.filter {
        search.isBlank() || "${it.firstName} ${it.lastName}".contains(search, true) || it.email.contains(search, true)
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) {
            Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        }
        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            AdminSearchBar(search, "Search customers...") { search = it }
            Text("${filtered.size} CUSTOMERS", fontSize = 10.sp, letterSpacing = 1.5.sp, color = DashSecText)
        }
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth().border(1.dp, DashBorder, RoundedCornerShape(6.dp)).clip(RoundedCornerShape(6.dp))
        ) {
            stickyHeader {
                AdminHeaderRow {
                    AdminHeaderCell("NAME", Modifier.weight(1f))
                    AdminHeaderCell("EMAIL", Modifier.weight(1f))
                    AdminHeaderCell("JOINED", Modifier.width(110.dp))
                    AdminHeaderCell("ACTIONS", Modifier.width(90.dp))
                }
            }
            items(filtered) { u ->
                AdminDataRow {
                    AdminDataCell(Modifier.weight(1f)) { Text("${u.firstName} ${u.lastName}", fontSize = 13.sp, color = ActionDark) }
                    AdminDataCell(Modifier.weight(1f)) { Text(u.email, fontSize = 12.sp, color = DashSecText) }
                    AdminDataCell(Modifier.width(110.dp)) { Text(fmtDate(u.createdAt), fontSize = 12.sp, color = DashSecText) }
                    AdminDataCell(Modifier.width(90.dp)) {
                        AdminBtnDelete { vm.deleteUser(u.userID) { e -> actionError = e } }
                    }
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(RowDivider))
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// STAFF
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AdminStaffTab(vm: AdminViewModel) {
    if (vm.isLoading) { LoadingBox(); return }
    var search by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<User?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }
    val managerRoles = vm.roles.filter { it.roleType != "Admin" && it.roleType != "Customer" }.map { it.roleType }
    val filtered = vm.staff.filter {
        search.isBlank() || "${it.firstName} ${it.lastName}".contains(search, true) || it.email.contains(search, true)
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) {
            Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        }
        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            AdminSearchBar(search, "Search staff...") { search = it }
            AdminBtnDark("+ ADD STAFF") { showAdd = true }
        }
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { u ->
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DashCardBg),
                    border = BorderStroke(1.dp, DashBorder),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${u.firstName} ${u.lastName}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = ActionDark,
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatusChip(u.isActive)
                        }
                        Text(u.email, fontSize = 12.sp, color = DashSecText)
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AdminRoleChip(u.roleType)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AdminBtnOutlined("EDIT") { editTarget = u }
                                AdminBtnOutlined(if (u.isActive) "DISABLE" else "ENABLE") {
                                    vm.toggleStatus(u) { e -> actionError = e }
                                }
                                AdminBtnDelete { vm.deleteUser(u.userID) { e -> actionError = e } }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    if (showAdd) {
        AddStaffDialog(
            roles = managerRoles,
            onDismiss = { showAdd = false },
            onConfirm = { req -> vm.createStaff(req) { e -> actionError = e; if (e == null) showAdd = false } }
        )
    }
    editTarget?.let { u ->
        EditStaffDialog(
            user = u,
            roles = managerRoles,
            onDismiss = { editTarget = null },
            onConfirm = { req, role -> vm.updateUser(u.userID, req, role) { e -> actionError = e; if (e == null) editTarget = null } }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// PRODUCTS  (Women / Men / Kids)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AdminProductsTab(vm: AdminViewModel, section: String) {
    if (vm.isLoading) { LoadingBox(); return }
    var search by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    var editWomen by remember { mutableStateOf<WomenCard?>(null) }
    var editMen   by remember { mutableStateOf<MenCard?>(null) }
    var editKids  by remember { mutableStateOf<KidsCards?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) {
            Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        }
        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            AdminSearchBar(search, "Search products...") { search = it }
            AdminBtnDark("+ ADD PRODUCT") { showAdd = true }
        }

        when (section) {
            "women" -> {
                val list = vm.womenCards.filter { search.isBlank() || it.title.contains(search, true) }
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
                                    Text(p.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ActionDark, maxLines = 2)
                                    Text("€${"%.2f".format(p.price)} · ${p.category}", fontSize = 12.sp, color = DashSecText)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            AdminBtnOutlined("EDIT") { editWomen = p }
                                            AdminBtnDelete { vm.deleteWomenCard(p.id) { e -> actionError = e } }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "men" -> {
                val list = vm.menCards.filter { search.isBlank() || it.title.contains(search, true) }
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
                                    Text(p.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ActionDark, maxLines = 2)
                                    Text("€${"%.2f".format(p.price)} · ${p.menCategoryName}", fontSize = 12.sp, color = DashSecText)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            AdminBtnOutlined("EDIT") { editMen = p }
                                            AdminBtnDelete { vm.deleteMenCard(p.id) { e -> actionError = e } }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                val list = vm.kidsCards.filter { search.isBlank() || it.title.contains(search, true) }
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
                                    Text(p.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ActionDark, maxLines = 2)
                                    Text("€${"%.2f".format(p.price)}", fontSize = 12.sp, color = DashSecText)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            AdminBtnOutlined("EDIT") { editKids = p }
                                            AdminBtnDelete { vm.deleteKidsCard(p.id) { e -> actionError = e } }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    if (showAdd) when (section) {
        "women" -> WomenProductDialog(null, vm.womenCategories, { showAdd = false }) { req ->
            vm.createWomenCard(req) { e -> actionError = e; if (e == null) showAdd = false }
        }
        "men"   -> MenProductDialog(null, vm.menCategories, { showAdd = false }) { req ->
            vm.createMenCard(req) { e -> actionError = e; if (e == null) showAdd = false }
        }
        else    -> KidsProductDialog(null, { showAdd = false }) { t, p, img, desc ->
            vm.createKidsCard(t, p, img, desc) { e -> actionError = e; if (e == null) showAdd = false }
        }
    }
    editWomen?.let { p ->
        WomenProductDialog(p, vm.womenCategories, { editWomen = null }) { req ->
            vm.updateWomenCard(p.id, req) { e -> actionError = e; if (e == null) editWomen = null }
        }
    }
    editMen?.let { p ->
        MenProductDialog(p, vm.menCategories, { editMen = null }) { req ->
            vm.updateMenCard(p.id, req) { e -> actionError = e; if (e == null) editMen = null }
        }
    }
    editKids?.let { p ->
        KidsProductDialog(p, { editKids = null }) { t, pr, img, desc ->
            vm.updateKidsCard(p.id, t, pr, img, desc) { e -> actionError = e; if (e == null) editKids = null }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// SALES
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AdminSalesTab(vm: AdminViewModel) {
    if (vm.isLoading) { LoadingBox(); return }
    var salesSection by remember { mutableIntStateOf(0) }
    var search by remember { mutableStateOf("") }
    var actionError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        if (actionError != null) {
            Text(actionError!!, color = DashRedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        }
        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("WOMEN", "MEN", "KIDS").forEachIndexed { i, label ->
                    if (salesSection == i) {
                        Button(
                            onClick = {},
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ActionDark),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) { Text(label, fontSize = 10.sp, letterSpacing = 1.sp) }
                    } else {
                        OutlinedButton(
                            onClick = { salesSection = i },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, DashBorder),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DashSecText)
                        ) { Text(label, fontSize = 10.sp, letterSpacing = 1.sp) }
                    }
                }
            }
            AdminSearchBar(search, "Search products...") { search = it }
        }

        when (salesSection) {
            0 -> {
                val list = vm.womenCards.filter { (it.discountPercent ?: 0) > 0 && (search.isBlank() || it.title.contains(search, true)) }
                val hScroll = rememberScrollState()
                val vScroll = rememberScrollState()
                Box(Modifier.weight(1f).fillMaxWidth().border(1.dp, DashBorder, RoundedCornerShape(6.dp)).clip(RoundedCornerShape(6.dp))) {
                    Column(Modifier.horizontalScroll(hScroll).verticalScroll(vScroll)) {
                        Row(Modifier.width(600.dp).background(TableHeaderBg).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.width(72.dp))
                            AdminHeaderCell("TITLE", Modifier.weight(1f))
                            AdminHeaderCell("ORIGINAL PRICE", Modifier.width(130.dp))
                            AdminHeaderCell("SALE", Modifier.width(100.dp))
                            AdminHeaderCell("ACTIONS", Modifier.width(200.dp))
                        }
                        list.forEach { p ->
                            Row(Modifier.width(600.dp).background(DashCardBg).padding(horizontal = 12.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                AdminThumb(p.imageUrl)
                                AdminDataCell(Modifier.weight(1f)) { Text(p.title, fontSize = 13.sp, color = ActionDark, maxLines = 1) }
                                AdminDataCell(Modifier.width(130.dp)) { Text("€${"%.2f".format(p.price)}", fontSize = 12.sp, color = DashSecText) }
                                AdminDataCell(Modifier.width(100.dp)) { AdminSaleBadge("${p.discountPercent ?: 0}% OFF") }
                                AdminDataCell(Modifier.width(200.dp)) { WomenSaleActions(p, vm) { e -> actionError = e } }
                            }
                            Box(Modifier.width(600.dp).height(1.dp).background(RowDivider))
                        }
                    }
                }
            }
            1 -> {
                val list = vm.menCards.filter { (it.discountPercent ?: 0) > 0 && (search.isBlank() || it.title.contains(search, true)) }
                val hScroll = rememberScrollState()
                val vScroll = rememberScrollState()
                Box(Modifier.weight(1f).fillMaxWidth().border(1.dp, DashBorder, RoundedCornerShape(6.dp)).clip(RoundedCornerShape(6.dp))) {
                    Column(Modifier.horizontalScroll(hScroll).verticalScroll(vScroll)) {
                        Row(Modifier.width(600.dp).background(TableHeaderBg).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.width(72.dp))
                            AdminHeaderCell("TITLE", Modifier.weight(1f))
                            AdminHeaderCell("ORIGINAL PRICE", Modifier.width(130.dp))
                            AdminHeaderCell("SALE", Modifier.width(100.dp))
                            AdminHeaderCell("ACTIONS", Modifier.width(200.dp))
                        }
                        list.forEach { p ->
                            Row(Modifier.width(600.dp).background(DashCardBg).padding(horizontal = 12.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                AdminThumb(p.imageUrl)
                                AdminDataCell(Modifier.weight(1f)) { Text(p.title, fontSize = 13.sp, color = ActionDark, maxLines = 1) }
                                AdminDataCell(Modifier.width(130.dp)) { Text("€${"%.2f".format(p.price)}", fontSize = 12.sp, color = DashSecText) }
                                AdminDataCell(Modifier.width(100.dp)) { AdminSaleBadge("${p.discountPercent ?: 0}% OFF") }
                                AdminDataCell(Modifier.width(200.dp)) { MenSaleActions(p, vm) { e -> actionError = e } }
                            }
                            Box(Modifier.width(600.dp).height(1.dp).background(RowDivider))
                        }
                    }
                }
            }
            else -> {
                val list = vm.kidsCards.filter { (it.discountPercent ?: 0) > 0 && (search.isBlank() || it.title.contains(search, true)) }
                val hScroll = rememberScrollState()
                val vScroll = rememberScrollState()
                Box(Modifier.weight(1f).fillMaxWidth().border(1.dp, DashBorder, RoundedCornerShape(6.dp)).clip(RoundedCornerShape(6.dp))) {
                    Column(Modifier.horizontalScroll(hScroll).verticalScroll(vScroll)) {
                        Row(Modifier.width(600.dp).background(TableHeaderBg).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.width(72.dp))
                            AdminHeaderCell("TITLE", Modifier.weight(1f))
                            AdminHeaderCell("ORIGINAL PRICE", Modifier.width(130.dp))
                            AdminHeaderCell("SALE", Modifier.width(100.dp))
                            AdminHeaderCell("ACTIONS", Modifier.width(200.dp))
                        }
                        list.forEach { p ->
                            Row(Modifier.width(600.dp).background(DashCardBg).padding(horizontal = 12.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                AdminThumb(p.imageUrl)
                                AdminDataCell(Modifier.weight(1f)) { Text(p.title, fontSize = 13.sp, color = ActionDark, maxLines = 1) }
                                AdminDataCell(Modifier.width(130.dp)) { Text("€${"%.2f".format(p.price)}", fontSize = 12.sp, color = DashSecText) }
                                AdminDataCell(Modifier.width(100.dp)) { AdminSaleBadge("${p.discountPercent ?: 0}% OFF") }
                                AdminDataCell(Modifier.width(200.dp)) { KidsSaleActions(p, vm) { e -> actionError = e } }
                            }
                            Box(Modifier.width(600.dp).height(1.dp).background(RowDivider))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

// ─── Per-row sale action composables ─────────────────────────────────────────

@Composable
private fun WomenSaleActions(p: WomenCard, vm: AdminViewModel, onError: (String?) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val pct = p.discountPercent ?: 0
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        AdminBtnDark(if (pct > 0) "EDIT SALE" else "SET SALE") { showDialog = true }
        if (pct > 0) AdminBtnOutlined("REMOVE") { vm.setWomenDiscount(p.id, 0) { e -> onError(e) } }
    }
    if (showDialog) {
        SaleDialog(p.title, pct, { showDialog = false }) { newPct ->
            vm.setWomenDiscount(p.id, newPct) { e -> onError(e) }
            showDialog = false
        }
    }
}

@Composable
private fun MenSaleActions(p: MenCard, vm: AdminViewModel, onError: (String?) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val pct = p.discountPercent ?: 0
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        AdminBtnDark(if (pct > 0) "EDIT SALE" else "SET SALE") { showDialog = true }
        if (pct > 0) AdminBtnOutlined("REMOVE") { vm.setMenDiscount(p, 0) { e -> onError(e) } }
    }
    if (showDialog) {
        SaleDialog(p.title, pct, { showDialog = false }) { newPct ->
            vm.setMenDiscount(p, newPct) { e -> onError(e) }
            showDialog = false
        }
    }
}

@Composable
private fun KidsSaleActions(p: KidsCards, vm: AdminViewModel, onError: (String?) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val pct = p.discountPercent ?: 0
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        AdminBtnDark(if (pct > 0) "EDIT SALE" else "SET SALE") { showDialog = true }
        if (pct > 0) AdminBtnOutlined("REMOVE") { vm.setKidsDiscount(p.id, 0) { e -> onError(e) } }
    }
    if (showDialog) {
        SaleDialog(p.title, pct, { showDialog = false }) { newPct ->
            vm.setKidsDiscount(p.id, newPct) { e -> onError(e) }
            showDialog = false
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Shared UI primitives
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AdminHeaderRow(cells: @Composable RowScope.() -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(TableHeaderBg).padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) { cells() }
}

@Composable
private fun RowScope.AdminHeaderCell(label: String, modifier: Modifier = Modifier) {
    Text(label, modifier.padding(horizontal = 4.dp), fontSize = 9.sp, letterSpacing = 1.5.sp, color = DashSecText)
}

@Composable
private fun AdminDataRow(cells: @Composable RowScope.() -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(DashCardBg).padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) { cells() }
}

@Composable
private fun RowScope.AdminDataCell(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.padding(horizontal = 4.dp)) { content() }
}

@Composable
private fun RowScope.AdminThumb(url: String) {
    Box(Modifier.width(72.dp).padding(end = 12.dp), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = url, contentDescription = null,
            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun AdminSaleBadge(label: String) {
    Text(label, fontSize = 9.sp, color = DashGreenText,
        modifier = Modifier.background(DashGreenBg, RoundedCornerShape(20.dp)).padding(horizontal = 8.dp, vertical = 3.dp))
}

@Composable
private fun AdminRoleChip(role: String) {
    Text(role, fontSize = 9.sp, color = roleText(role),
        modifier = Modifier.background(roleBg(role), RoundedCornerShape(20.dp)).padding(horizontal = 8.dp, vertical = 3.dp))
}

@Composable
private fun AdminStatusChip(active: Boolean) {
    Text(if (active) "ACTIVE" else "INACTIVE", fontSize = 9.sp,
        color = if (active) ChipActiveText else DashRedText,
        modifier = Modifier
            .background(if (active) ChipActiveBg else ChipInactiveBg, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp))
}

@Composable
private fun AdminBtnDelete(onClick: () -> Unit) {
    Button(onClick = onClick, shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ActionRed),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        modifier = Modifier.height(30.dp)
    ) { Text("DELETE", fontSize = 9.sp, letterSpacing = 1.sp) }
}

@Composable
private fun AdminBtnOutlined(label: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFD0C4BC)),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        modifier = Modifier.height(30.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ActionDark)
    ) { Text(label, fontSize = 9.sp, letterSpacing = 1.sp) }
}

@Composable
private fun AdminBtnDark(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ActionDark),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier.height(36.dp)
    ) { Text(label, fontSize = 10.sp, letterSpacing = 1.sp) }
}

@Composable
private fun AdminSearchBar(value: String, placeholder: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFBBABA4), fontSize = 12.sp) },
        modifier = Modifier.width(260.dp).height(44.dp),
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DashBorder,
            focusedBorderColor = ActionDark,
            unfocusedContainerColor = DashCardBg,
            focusedContainerColor = DashCardBg
        )
    )
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        CircularProgressIndicator(color = DashSecText)
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Dialogs
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AddStaffDialog(roles: List<String>, onDismiss: () -> Unit, onConfirm: (CreateStaffRequest) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var role      by remember { mutableStateOf(roles.firstOrNull() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ADD STAFF", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(firstName, { firstName = it }, label = { Text("First Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(lastName,  { lastName  = it }, label = { Text("Last Name") },  singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(email,     { email     = it }, label = { Text("Email") },      singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(password,  { password  = it }, label = { Text("Password") },   singleLine = true, modifier = Modifier.fillMaxWidth())
                if (roles.isNotEmpty()) {
                    Text("Role", fontSize = 11.sp, color = DashSecText)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        roles.forEach { r ->
                            FilterChip(selected = role == r, onClick = { role = r }, label = { Text(r, fontSize = 10.sp) })
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(CreateStaffRequest(firstName, lastName, email, password, role)) }) { Text("ADD") }
        },
        dismissButton = { TextButton(onDismiss) { Text("CANCEL") } }
    )
}

@Composable
private fun EditStaffDialog(user: User, roles: List<String>, onDismiss: () -> Unit, onConfirm: (UpdateUserRequest, String) -> Unit) {
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName  by remember { mutableStateOf(user.lastName) }
    var email     by remember { mutableStateOf(user.email) }
    var role      by remember { mutableStateOf(user.roleType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("EDIT STAFF", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(firstName, { firstName = it }, label = { Text("First Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(lastName,  { lastName  = it }, label = { Text("Last Name") },  singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(email,     { email     = it }, label = { Text("Email") },      singleLine = true, modifier = Modifier.fillMaxWidth())
                if (roles.isNotEmpty()) {
                    Text("Role", fontSize = 11.sp, color = DashSecText)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        roles.forEach { r ->
                            FilterChip(selected = role == r, onClick = { role = r }, label = { Text(r, fontSize = 10.sp) })
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(UpdateUserRequest(firstName, lastName, email), role) }) { Text("SAVE") }
        },
        dismissButton = { TextButton(onDismiss) { Text("CANCEL") } }
    )
}

@Composable
private fun WomenProductDialog(card: WomenCard?, categories: List<WomenCategory>, onDismiss: () -> Unit, onConfirm: (WomenCardRequest) -> Unit) {
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
private fun MenProductDialog(card: MenCard?, categories: List<MenCategory>, onDismiss: () -> Unit, onConfirm: (MenCardRequest) -> Unit) {
    var title       by remember { mutableStateOf(card?.title ?: "") }
    var price       by remember { mutableStateOf(card?.price?.toString() ?: "") }
    var imageUrl    by remember { mutableStateOf(card?.imageUrl ?: "") }
    var description by remember { mutableStateOf(card?.description ?: "") }
    var catId       by remember { mutableIntStateOf(card?.menCategoryId ?: categories.firstOrNull()?.id ?: 0) }

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
                onConfirm(MenCardRequest(title, description, p, imageUrl.ifBlank { null }, catId))
            }) { Text("SAVE") }
        },
        dismissButton = { TextButton(onDismiss) { Text("CANCEL") } }
    )
}

@Composable
private fun KidsProductDialog(card: KidsCards?, onDismiss: () -> Unit, onConfirm: (String, Double, String, String) -> Unit) {
    var title       by remember { mutableStateOf(card?.title ?: "") }
    var price       by remember { mutableStateOf(card?.price?.toString() ?: "") }
    var imageUrl    by remember { mutableStateOf(card?.imageUrl ?: "") }
    var description by remember { mutableStateOf(card?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (card == null) "ADD KIDS PRODUCT" else "EDIT KIDS PRODUCT", fontSize = 11.sp, letterSpacing = 2.sp) },
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
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val p = price.toDoubleOrNull() ?: return@TextButton
                onConfirm(title, p, imageUrl, description)
            }) { Text("SAVE") }
        },
        dismissButton = { TextButton(onDismiss) { Text("CANCEL") } }
    )
}

@Composable
private fun SaleDialog(productTitle: String, currentPct: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var pct by remember { mutableStateOf(if (currentPct > 0) currentPct.toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("SET SALE", fontSize = 11.sp, letterSpacing = 2.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(productTitle, fontSize = 13.sp, color = ActionDark)
                OutlinedTextField(pct, { pct = it }, label = { Text("Discount %") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(pct.toIntOrNull() ?: 0) }) { Text("APPLY") }
        },
        dismissButton = { TextButton(onDismiss) { Text("CANCEL") } }
    )
}

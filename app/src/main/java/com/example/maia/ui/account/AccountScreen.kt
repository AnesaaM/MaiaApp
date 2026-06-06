package com.example.maia.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.maia.data.TokenManager
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.MaiaAccent
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory
import com.example.maia.viewmodel.OrderViewModel

@Composable
fun AccountScreen(navController: NavController, tokenManager: TokenManager) {
    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    val orderVm: OrderViewModel = viewModel()
    val orders = orderVm.orders.value

    LaunchedEffect(Unit) { orderVm.loadOrders() }

    val username = tokenManager.getUsername() ?: "Guest"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "MAIA QR",
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = MaiaText,
                fontWeight = FontWeight.Medium
            )
            Text(
                "SETTINGS",
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = MaiaTextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        HorizontalDivider(color = Color(0xFFE8DDD8), thickness = 0.5.dp)

        Spacer(Modifier.height(24.dp))

        // User greeting
        Text(
            username.uppercase(),
            modifier = Modifier.padding(horizontal = 24.dp),
            fontSize = 22.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = MaiaText,
            letterSpacing = 3.sp
        )

        Spacer(Modifier.height(32.dp))

        // Menu items
        val menuItems = listOf("PURCHASES", "FAVORITES", "CONTACT DATA", "STORES", "NOTIFICATIONS")
        menuItems.forEach { item ->
            AccountMenuItem(label = item, onClick = { })
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color(0xFFEDE8E3),
                thickness = 0.5.dp
            )
        }

        // SPECIAL PRICES
        Spacer(Modifier.height(8.dp))
        Text(
            "SPECIAL PRICES",
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .clickable { },
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            color = MaiaAccent,
            fontWeight = FontWeight.Medium
        )

        HorizontalDivider(color = Color(0xFFE8DDD8), thickness = 0.5.dp)

        // Orders section
        if (orders.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(
                "RECENT ORDERS",
                modifier = Modifier.padding(horizontal = 24.dp),
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = MaiaTextSecondary
            )
            Spacer(Modifier.height(12.dp))
            orders.take(3).forEach { order ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Order #${order.id}", fontSize = 12.sp, color = MaiaText)
                    Text(order.status, fontSize = 12.sp, color = MaiaTextSecondary)
                    Text("${String.format("%.0f", order.totalAmount)} EUR", fontSize = 12.sp, color = MaiaText)
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = Color(0xFFEDE8E3),
                    thickness = 0.5.dp
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Logout
        TextButton(
            onClick = {
                authVm.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("LOG OUT", fontSize = 11.sp, letterSpacing = 2.sp, color = MaiaTextSecondary)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AccountMenuItem(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp,
            color = MaiaText
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaiaTextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

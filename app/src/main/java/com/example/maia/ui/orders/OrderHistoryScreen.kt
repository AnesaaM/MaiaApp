package com.example.maia.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.model.order.Order
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaBorder
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.viewmodel.OrderViewModel

@Preview(showBackground = true, name = "Order History Screen")
@Composable
fun OrderHistoryScreenPreview() {
    OrderHistoryScreen(navController = rememberNavController())
}

@Composable
fun OrderHistoryScreen(navController: NavController) {
    val vm: OrderViewModel = viewModel()
    LaunchedEffect(Unit) { vm.loadOrders() }

    val orders = vm.orders.value
    val isLoading = vm.isLoading.value
    val error = vm.error.value
    val blobColor = MaiaBlob

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        // Blob header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
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
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaiaText)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "MAIA",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = MaiaText,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(48.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "PURCHASES",
            fontSize = 11.sp,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Medium,
            color = MaiaTextSecondary,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaiaText, strokeWidth = 1.5.dp)
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Could not load orders", color = MaiaTextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { vm.loadOrders() }) {
                        Text("RETRY", fontSize = 10.sp, letterSpacing = 2.sp, color = MaiaText)
                    }
                }
            }
            orders.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No orders yet",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        color = MaiaText,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Your purchase history will appear here",
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = MaiaTextSecondary
                    )
                }
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderRow(order = order)
                    HorizontalDivider(color = MaiaBorder, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun OrderRow(order: Order) {
    val statusColor = when (order.status.lowercase()) {
        "delivered"  -> Color(0xFF2E7D32)
        "cancelled"  -> Color(0xFFB5381A)
        "processing" -> Color(0xFFE65100)
        "shipped"    -> Color(0xFF1565C0)
        else         -> MaiaTextSecondary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "MAIA-${order.id.toString().padStart(6, '0')}",
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium,
                color = MaiaText
            )
            Text(
                order.status.uppercase(),
                fontSize = 9.sp,
                letterSpacing = 1.5.sp,
                color = statusColor
            )
        }

        Spacer(Modifier.height(6.dp))

        order.items?.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    item.product?.title ?: "Product #${item.productId}",
                    fontSize = 12.sp,
                    color = MaiaTextSecondary,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    maxLines = 1
                )
                Text(
                    "x${item.quantity}  €${"%.0f".format(item.price * item.quantity)}",
                    fontSize = 12.sp,
                    color = MaiaTextSecondary
                )
            }
            Spacer(Modifier.height(2.dp))
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (order.createdAt.isNotBlank()) {
                Text(order.createdAt.take(10), fontSize = 10.sp, color = MaiaTextSecondary, letterSpacing = 0.5.sp)
            }
            Text(
                "€${"%.0f".format(order.totalAmount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaiaText
            )
        }
    }
}

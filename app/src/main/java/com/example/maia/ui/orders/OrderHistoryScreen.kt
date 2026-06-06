package com.example.maia.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.maia.model.order.Order
import com.example.maia.viewmodel.OrderViewModel

private val Purple = Color(0xFF6C5CE7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen() {
    val vm: OrderViewModel = viewModel()

    LaunchedEffect(Unit) { vm.loadOrders() }

    val orders = vm.orders.value
    val isLoading = vm.isLoading.value
    val error = vm.error.value

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Orders", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Purple)
                error != null -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Could not load orders", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { vm.loadOrders() }) { Text("Retry") }
                }
                orders.isEmpty() -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No orders yet", fontSize = 18.sp, color = Color.Gray)
                    Text("Your order history will appear here", fontSize = 14.sp, color = Color.LightGray)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        OrderCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    val statusColor = when (order.status.lowercase()) {
        "delivered" -> Color(0xFF2E7D32)
        "cancelled" -> Color(0xFFC62828)
        "processing" -> Color(0xFFE65100)
        else -> Color(0xFF1565C0)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        order.status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            order.items?.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        item.product?.title ?: "Product #${item.productId}",
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text("x${item.quantity}", fontSize = 14.sp, color = Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    Text("€ ${String.format("%.2f", item.price * item.quantity)}", fontSize = 14.sp)
                }
                Spacer(Modifier.height(4.dp))
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.SemiBold)
                Text(
                    "€ ${String.format("%.2f", order.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    color = Purple
                )
            }

            if (order.createdAt.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    order.createdAt.take(10),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

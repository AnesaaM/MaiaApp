package com.example.maia.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.maia.data.TokenManager
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaButton
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.viewmodel.CartViewModel

@Composable
fun CheckoutScreen(navController: NavController, cartViewModel: CartViewModel, tokenManager: TokenManager) {
    var fullName by remember { mutableStateOf(tokenManager.getUsername() ?: "") }
    var email by remember { mutableStateOf(tokenManager.getEmail() ?: "") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    val isLoading = cartViewModel.isLoading.value
    val error = cartViewModel.error.value
    val orderPlaced = cartViewModel.orderPlaced.value
    val placedOrder = cartViewModel.placedOrder.value

    LaunchedEffect(orderPlaced) {
        if (orderPlaced && placedOrder != null) {
            val orderRef = "MAIA-${placedOrder.id.toString().padStart(6, '0')}"
            cartViewModel.clearOrderPlaced()
            navController.navigate(Screen.OrderConfirmed.createRoute(orderRef)) {
                popUpTo(Screen.Cart.route) { inclusive = true }
            }
        }
    }

    val formValid = fullName.isNotBlank() && email.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        // Header
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
            Text(
                "CHECKOUT",
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaiaText
            )
        }

        HorizontalDivider(color = Color(0xFFEDE8E3), thickness = 0.5.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionLabel("DELIVERY INFORMATION")

            CheckoutField("Full Name", fullName) { fullName = it }
            CheckoutField("Email", email) { email = it }
            CheckoutField("Phone Number", phone) { phone = it }

            Spacer(Modifier.height(4.dp))
            SectionLabel("SHIPPING ADDRESS")

            CheckoutField("Address", address) { address = it }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    CheckoutField("City", city) { city = it }
                }
                Box(Modifier.weight(1f)) {
                    CheckoutField("Postal Code", postalCode) { postalCode = it }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Order summary
            SectionLabel("ORDER SUMMARY")
            val total = cartViewModel.totalPrice
            val count = cartViewModel.itemCount
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$count item${if (count != 1) "s" else ""}", fontSize = 12.sp, color = MaiaTextSecondary)
                Text("${String.format("%.0f", total)} EUR", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaiaText)
            }

            Spacer(Modifier.height(8.dp))

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
            }

            Button(
                onClick = { if (formValid) cartViewModel.placeOrder(email, fullName) },
                enabled = formValid && !isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaiaButton,
                    disabledContainerColor = Color(0xFFD8C8C2)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 1.5.dp, modifier = Modifier.size(18.dp))
                } else {
                    Text("CONFIRM ORDER", letterSpacing = 2.sp, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 9.sp, letterSpacing = 2.sp, color = MaiaTextSecondary, fontWeight = FontWeight.Medium)
}

@Composable
private fun CheckoutField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFD8C8C2),
            focusedBorderColor = MaiaText,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedLabelColor = MaiaTextSecondary,
            focusedLabelColor = MaiaText
        )
    )
}

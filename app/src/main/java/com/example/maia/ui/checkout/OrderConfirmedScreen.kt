package com.example.maia.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.maia.data.TokenManager
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.MaiaButton
import com.example.maia.ui.components.MaiaText

@Composable
fun OrderConfirmedScreen(
    navController: NavController,
    tokenManager: TokenManager,
    orderRef: String
) {
    val userName = tokenManager.getUsername()?.lowercase() ?: "customer"
    val userEmail = tokenManager.getEmail() ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Text(
                text = orderRef,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                color = Color(0xFFB5956B),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Order Confirmed",
                fontSize = 40.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Normal,
                color = MaiaText,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF9E8E86), fontSize = 13.sp)) {
                        append("Thank you, $userName. We'll send updates to ")
                    }
                    withStyle(SpanStyle(color = Color(0xFFB5956B), fontSize = 13.sp)) {
                        append(userEmail)
                    }
                    withStyle(SpanStyle(color = Color(0xFF9E8E86), fontSize = 13.sp)) {
                        append(".")
                    }
                },
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Shop.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaiaButton)
            ) {
                Text("CONTINUE SHOPPING", letterSpacing = 2.sp, fontSize = 11.sp, color = Color.White)
            }
        }
    }
}

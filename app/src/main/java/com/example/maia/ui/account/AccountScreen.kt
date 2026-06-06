package com.example.maia.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.maia.data.TokenManager
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.MaiaAccent
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaBlob
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary
import com.example.maia.viewmodel.AuthViewModel
import com.example.maia.viewmodel.AuthViewModelFactory
import com.example.maia.viewmodel.ProductViewModel

@Preview(showBackground = true, name = "Account Screen")
@Composable
fun AccountScreenPreview() {
    val context = LocalContext.current
    AccountScreen(
        navController = rememberNavController(),
        tokenManager = TokenManager(context)
    )
}

@Composable
fun AccountScreen(navController: NavController, tokenManager: TokenManager) {
    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))
    val productVm: ProductViewModel = viewModel()
    val blobColor = MaiaBlob

    val username = tokenManager.getUsername() ?: "guest"
    val products = productVm.allProducts.value.take(2)

    val menuItems = listOf(
        "PURCHASES" to Screen.Orders.route,
        "CONTACT DATA" to null,
        "STORES" to null,
        "NOTIFICATIONS" to Screen.Notifications.route
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaiaBackground)
    ) {
        // Header blob
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
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // MAIA QR left
                Text(
                    "MAIA QR  ⊞",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = MaiaText,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.weight(1f))

                // MAIA center
                Text(
                    "MAIA",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = MaiaText,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.weight(1f))

                // Username right
                Text(
                    username.lowercase(),
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = MaiaText
                )
            }
        }

        // Content: menu left + images right
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            // Left: menu items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                menuItems.forEach { (label, route) ->
                    Text(
                        text = label,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        color = MaiaText,
                        letterSpacing = 1.sp,
                        modifier = Modifier.clickable {
                            route?.let { navController.navigate(it) }
                        }
                    )
                }

                // LOG OUT in red
                Text(
                    text = "LOG OUT",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light,
                    color = MaiaAccent,
                    letterSpacing = 1.sp,
                    modifier = Modifier.clickable {
                        authVm.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Right: two editorial photos
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val imageModifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.65f)
                    .background(Color(0xFFEDE8E3))

                if (products.size >= 2) {
                    AsyncImage(
                        model = products[0].imageUrl,
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                    AsyncImage(
                        model = products[1].imageUrl,
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = imageModifier)
                    Box(modifier = imageModifier)
                }
            }
        }
    }
}

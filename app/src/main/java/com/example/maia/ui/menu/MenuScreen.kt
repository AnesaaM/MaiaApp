package com.example.maia.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.maia.navigation.Screen
import com.example.maia.ui.components.MaiaBackground
import com.example.maia.ui.components.MaiaText
import com.example.maia.ui.components.MaiaTextSecondary

@Preview(showBackground = true, name = "Menu Screen")
@Composable
fun MenuScreenPreview() {
    MenuScreen(navController = androidx.navigation.compose.rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController) {

    val categories = listOf(
        "WOMAN" to "New arrivals, dresses, tops & more",
        "MAN"   to "Shirts, trousers, outerwear & more",
        "KIDS"  to "Clothing for boys & girls"
    )

    val links = listOf("ABOUT MAIA", "STORES", "SUSTAINABILITY", "CONTACT US")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaiaText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaiaBackground)
            )
        },
        containerColor = MaiaBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                "SHOP",
                modifier = Modifier.padding(horizontal = 24.dp),
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = MaiaTextSecondary
            )

            Spacer(Modifier.height(12.dp))

            categories.forEach { (label, subtitle) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            label,
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Light,
                            color = MaiaText,
                            letterSpacing = 2.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(subtitle, fontSize = 11.sp, color = MaiaTextSecondary)
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaiaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = Color(0xFFEDE8E3),
                    thickness = 0.5.dp
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "EXPLORE",
                modifier = Modifier.padding(horizontal = 24.dp),
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = MaiaTextSecondary
            )

            Spacer(Modifier.height(12.dp))

            links.forEach { label ->
                Text(
                    label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {}
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    fontSize = 13.sp,
                    letterSpacing = 1.5.sp,
                    color = MaiaText
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = Color(0xFFEDE8E3),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

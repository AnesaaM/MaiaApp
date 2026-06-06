package com.example.maia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.maia.navigation.Screen

@Preview(showBackground = true, name = "Bottom Nav Bar")
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(navController = rememberNavController(), currentRoute = Screen.Home.route, cartCount = 3)
}

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String?, cartCount: Int = 0) {
    Column {
        HorizontalDivider(color = Color(0xFFE8DDD8), thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaiaBackground)
                .navigationBarsPadding()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavIcon(
                icon = Icons.Default.Home,
                selected = currentRoute == Screen.Home.route,
                onClick = { navController.navigateTo(currentRoute, Screen.Home.route) }
            )
            NavIcon(
                icon = Icons.Default.Menu,
                selected = currentRoute == Screen.Shop.route,
                onClick = { navController.navigateTo(currentRoute, Screen.Shop.route) }
            )
            NavIcon(
                icon = Icons.Default.Search,
                selected = false,
                onClick = { navController.navigateTo(currentRoute, Screen.Shop.route) }
            )
            Box {
                NavIcon(
                    icon = Icons.Default.ShoppingBag,
                    selected = currentRoute == Screen.Cart.route,
                    onClick = { navController.navigateTo(currentRoute, Screen.Cart.route) }
                )
                if (cartCount > 0) {
                    Badge(
                        modifier = Modifier.align(Alignment.TopEnd).offset(x = 2.dp, y = (-2).dp),
                        containerColor = MaiaText,
                        contentColor = MaiaBackground
                    ) {
                        Text("$cartCount", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            NavIcon(
                icon = Icons.Default.Person,
                selected = currentRoute == Screen.Account.route,
                onClick = { navController.navigateTo(currentRoute, Screen.Account.route) }
            )
        }
    }
}

@Composable
private fun NavIcon(icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaiaText else Color(0xFFB0A09A),
            modifier = Modifier.size(22.dp)
        )
    }
}

private fun NavController.navigateTo(currentRoute: String?, targetRoute: String) {
    if (currentRoute != targetRoute) {
        navigate(targetRoute) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

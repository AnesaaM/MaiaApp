package com.example.maia.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.maia.data.TokenManager
import com.example.maia.ui.account.AccountScreen
import com.example.maia.ui.auth.ForgotPasswordScreen
import com.example.maia.ui.auth.LoginScreen
import com.example.maia.ui.auth.RegisterScreen
import com.example.maia.ui.auth.VerificationScreen
import com.example.maia.ui.cart.CartScreen
import com.example.maia.ui.checkout.CheckoutScreen
import com.example.maia.ui.components.BottomNavBar
import com.example.maia.ui.home.HomeScreen
import com.example.maia.ui.menu.MenuScreen
import com.example.maia.ui.notifications.NotificationsScreen
import com.example.maia.ui.search.SearchScreen
import com.example.maia.ui.orders.OrderHistoryScreen
import com.example.maia.ui.shop.ShopScreen
import com.example.maia.ui.wishlist.WishlistScreen
import com.example.maia.viewmodel.CartViewModel
import com.example.maia.viewmodel.WishlistViewModel

private val mainRoutes = setOf(
    Screen.Home.route, Screen.Shop.route, "shop/{s}", "shop/{s}/{catId}",
    Screen.Menu.route, Screen.Search.route, Screen.Cart.route, Screen.Account.route,
    Screen.Wishlist.route, Screen.Orders.route, Screen.Notifications.route
)

@Composable
fun NavGraph(navController: NavHostController, tokenManager: TokenManager) {
    val cartViewModel: CartViewModel = viewModel()
    val wishlistViewModel: WishlistViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val startDestination = if (tokenManager.isLoggedIn()) Screen.Home.route else Screen.Login.route

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (currentRoute in mainRoutes) {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    cartCount = cartViewModel.itemCount
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(navController = navController, tokenManager = tokenManager)
            }
            composable(Screen.Register.route) {
                RegisterScreen(navController = navController, tokenManager = tokenManager)
            }
            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(navController = navController, tokenManager = tokenManager)
            }
            composable(Screen.VerifyEmail.route) { backStackEntry ->
                val raw = backStackEntry.arguments?.getString("email") ?: ""
                val email = java.net.URLDecoder.decode(raw, "UTF-8")
                VerificationScreen(
                    navController = navController,
                    email = email,
                    tokenManager = tokenManager
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Shop.route) {
                ShopScreen(
                    navController = navController,
                    tokenManager = tokenManager,
                    cartViewModel = cartViewModel,
                    wishlistViewModel = wishlistViewModel
                )
            }
            composable("shop/{s}") { backStackEntry ->
                val s = backStackEntry.arguments?.getString("s")?.toIntOrNull() ?: 0
                ShopScreen(
                    navController = navController,
                    tokenManager = tokenManager,
                    cartViewModel = cartViewModel,
                    wishlistViewModel = wishlistViewModel,
                    initialSection = s
                )
            }
            composable("shop/{s}/{catId}") { backStackEntry ->
                val s = backStackEntry.arguments?.getString("s")?.toIntOrNull() ?: 0
                val catId = backStackEntry.arguments?.getString("catId")?.toIntOrNull() ?: 0
                ShopScreen(
                    navController = navController,
                    tokenManager = tokenManager,
                    cartViewModel = cartViewModel,
                    wishlistViewModel = wishlistViewModel,
                    initialSection = s,
                    categoryFilter = catId
                )
            }
            composable(Screen.Cart.route) {
                CartScreen(
                    navController = navController,
                    cartViewModel = cartViewModel,
                    wishlistViewModel = wishlistViewModel
                )
            }
            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    navController = navController,
                    cartViewModel = cartViewModel
                )
            }
            composable(Screen.Account.route) {
                AccountScreen(
                    navController = navController,
                    tokenManager = tokenManager
                )
            }
            composable(Screen.Orders.route) {
                OrderHistoryScreen()
            }
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    wishlistViewModel = wishlistViewModel,
                    cartViewModel = cartViewModel
                )
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen(navController = navController)
            }
            composable(Screen.Menu.route) {
                MenuScreen(navController = navController)
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    navController = navController,
                    cartViewModel = cartViewModel,
                    wishlistViewModel = wishlistViewModel
                )
            }
        }
    }
}

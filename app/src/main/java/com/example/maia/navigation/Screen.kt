package com.example.maia.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Wishlist : Screen("wishlist")
    object Orders : Screen("orders")
    object Account : Screen("account")
    object Notifications : Screen("notifications")
}

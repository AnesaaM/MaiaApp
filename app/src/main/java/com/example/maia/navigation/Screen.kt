package com.example.maia.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object VerifyEmail : Screen("verify_email/{email}") {
        fun createRoute(email: String) =
            "verify_email/${java.net.URLEncoder.encode(email, "UTF-8")}"
    }
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Wishlist : Screen("wishlist")
    object Orders : Screen("orders")
    object Account : Screen("account")
    object Notifications : Screen("notifications")
    object Menu : Screen("menu")
    object Shop : Screen("shop")
    object Search : Screen("search")
    object Checkout : Screen("checkout")
    object Stores : Screen("stores")
    object AdminDashboard : Screen("dashboard_admin")
    object SalesManagerDashboard : Screen("dashboard_sales")
    object WomenManagerDashboard : Screen("dashboard_women")
    object MenManagerDashboard : Screen("dashboard_men")
    object KidsManagerDashboard : Screen("dashboard_kids")
}

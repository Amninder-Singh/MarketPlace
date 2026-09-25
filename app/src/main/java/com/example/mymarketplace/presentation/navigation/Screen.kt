package com.example.mymarketplace.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object ProductList : Screen("product_list")
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    data object Favorites : Screen("favorites")

}

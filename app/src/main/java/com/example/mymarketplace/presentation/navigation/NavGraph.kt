package com.example.mymarketplace.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mymarketplace.presentation.favorites.FavoritesScreen
import com.example.mymarketplace.presentation.favorites.FavoritesViewModel
import com.example.mymarketplace.presentation.product_detail.ProductDetailScreen
import com.example.mymarketplace.presentation.product_detail.ProductDetailViewModel
import com.example.mymarketplace.presentation.product_list.ProductListScreen
import com.example.mymarketplace.presentation.product_list.ProductListViewModel
import com.example.mymarketplace.presentation.splash.SplashScreen

@Composable
fun MarketplaceNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProductList.route) {
            val viewModel: ProductListViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            ProductListScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }

        composable(Screen.ProductDetail.route) {
            val viewModel: ProductDetailViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            ProductDetailScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Favorites.route) {
            val viewModel: FavoritesViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            FavoritesScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

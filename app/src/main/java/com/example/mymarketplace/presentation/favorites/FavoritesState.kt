package com.example.mymarketplace.presentation.favorites

import com.example.mymarketplace.domain.model.Product

data class FavoritesState(
    val favoriteProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false
)

package com.example.mymarketplace.presentation.favorites

sealed interface FavoritesEvent {
    data class OnToggleFavorite(val productId: String) : FavoritesEvent
}

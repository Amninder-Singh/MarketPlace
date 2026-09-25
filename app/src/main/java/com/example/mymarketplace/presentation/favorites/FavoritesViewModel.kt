package com.example.mymarketplace.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymarketplace.domain.usecase.GetFavoriteProductsUseCase
import com.example.mymarketplace.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    val state: StateFlow<FavoritesState> = getFavoriteProductsUseCase()
        .map { products ->
            FavoritesState(
                favoriteProducts = products,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesState(isLoading = true)
        )

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.OnToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(event.productId)
                }
            }
        }
    }
}

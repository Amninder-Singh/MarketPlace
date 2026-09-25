package com.example.mymarketplace.presentation.product_list

import com.example.mymarketplace.domain.model.ProductCategory

sealed interface ProductListEvent {
    data class OnSearchQueryChange(val query: String) : ProductListEvent
    data class OnSelectCategory(val category: ProductCategory) : ProductListEvent
    data class OnToggleFavorite(val productId: String) : ProductListEvent
    data object OnRefreshSync : ProductListEvent
}

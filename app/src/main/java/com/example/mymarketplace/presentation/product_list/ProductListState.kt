package com.example.mymarketplace.presentation.product_list

import com.example.mymarketplace.data.sync.SyncState
import com.example.mymarketplace.domain.model.Product
import com.example.mymarketplace.domain.model.ProductCategory

data class ProductListState(
    val products: List<Product> = emptyList(),
    val selectedCategory: ProductCategory = ProductCategory.ALL,
    val searchQuery: String = "",
    val syncState: SyncState = SyncState.ONLINE,
    val isLoading: Boolean = false
)

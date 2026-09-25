package com.example.mymarketplace.domain.repository

import com.example.mymarketplace.domain.model.Product
import com.example.mymarketplace.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(category: ProductCategory? = null, searchQuery: String = ""): Flow<List<Product>>
    fun getProductById(productId: String): Flow<Product?>
    fun getFavoriteProducts(): Flow<List<Product>>
    suspend fun toggleFavorite(productId: String)
    suspend fun refreshProducts()
    suspend fun updateProductImage(productId: String, imageUrl: String, pendingUpload: Boolean)
    suspend fun updatePendingImageUpload(productId: String, pendingUpload: Boolean)
    fun scheduleImageUpload(productId: String, imageUri: String)
}

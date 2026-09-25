package com.example.mymarketplace.domain.usecase

import com.example.mymarketplace.domain.model.Product
import com.example.mymarketplace.domain.model.ProductCategory
import com.example.mymarketplace.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(category: ProductCategory? = null, searchQuery: String = ""): Flow<List<Product>> {
        return productRepository.getProducts(category, searchQuery)
    }
}

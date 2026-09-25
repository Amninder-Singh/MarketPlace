package com.example.mymarketplace.domain.usecase

import com.example.mymarketplace.domain.model.Product
import com.example.mymarketplace.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return productRepository.getFavoriteProducts()
    }
}

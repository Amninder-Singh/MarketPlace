package com.example.mymarketplace.domain.usecase

import com.example.mymarketplace.domain.model.Product
import com.example.mymarketplace.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: String): Flow<Product?> {
        return productRepository.getProductById(productId)
    }
}

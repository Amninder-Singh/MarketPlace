package com.example.mymarketplace.domain.usecase

import com.example.mymarketplace.domain.repository.ProductRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String) {
        productRepository.toggleFavorite(productId)
    }
}

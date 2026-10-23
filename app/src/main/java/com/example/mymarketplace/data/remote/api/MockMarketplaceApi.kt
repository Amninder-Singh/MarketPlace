package com.example.mymarketplace.data.remote.api

import com.example.mymarketplace.data.remote.model.ProductDto
import com.example.mymarketplace.data.remote.model.SyncRequestDto
import com.example.mymarketplace.data.remote.model.SyncResponseDto
import com.example.mymarketplace.domain.model.ProductCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockMarketplaceApi @Inject constructor() : MarketplaceApi {

    private val mockProductsMap = mutableMapOf<String, ProductDto>()

    init {
        populateInitial200Products()
    }

    override suspend fun getProducts(): List<ProductDto> {
        return mockProductsMap.values.toList()
    }

    override suspend fun getProductById(id: String): ProductDto? {
        return mockProductsMap[id]
    }

    override suspend fun createProduct(productDto: ProductDto): ProductDto {
        mockProductsMap[productDto.id] = productDto
        return productDto
    }

    override suspend fun updateProduct(id: String, productDto: ProductDto): ProductDto {
        mockProductsMap[id] = productDto
        return productDto
    }

    override suspend fun syncProducts(request: SyncRequestDto): SyncResponseDto {
        val syncedListings = mutableListOf<ProductDto>()

        request.createdItems.forEach { dto ->
            mockProductsMap[dto.id] = dto
            syncedListings.add(dto)
        }

        request.updatedItems.forEach { dto ->
            mockProductsMap[dto.id] = dto
            syncedListings.add(dto)
        }

        return SyncResponseDto(
            syncedListings = syncedListings,
            status = "SUCCESS",
            timestamp = System.currentTimeMillis()
        )
    }

    private fun populateInitial200Products() {
        for (i in 1..200) {
            val id = i.toString()
            mockProductsMap[id] = ProductDto(
                id = id,
                title = "Product #$id",
                description = "This is a great description for product $id",
                price = 10.0 + i,
                category = ProductCategory.ELECTRONICS.name,
                imageUrl = "https://picsum.photos/200?random=$id",
                rating = 4.5,
                stock = 10
            )
        }
    }
}

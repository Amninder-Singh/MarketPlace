package com.example.mymarketplace.data.remote.api

import com.example.mymarketplace.data.remote.model.ProductDto
import com.example.mymarketplace.data.remote.model.SyncRequestDto
import com.example.mymarketplace.data.remote.model.SyncResponseDto
import com.example.mymarketplace.domain.model.ProductCategory
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeMarketplaceApi @Inject constructor() : MarketplaceApi {

    private val mockProductsMap = ConcurrentHashMap<String, ProductDto>()

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
        if (mockProductsMap.isNotEmpty()) return

        val categories = arrayOf(
            ProductCategory.ELECTRONICS,
            ProductCategory.FASHION,
            ProductCategory.HOME,
            ProductCategory.BOOKS,
            ProductCategory.SPORTS
        )

        val baseNames = mapOf(
            ProductCategory.ELECTRONICS to listOf("Headphones", "Smart Watch", "Bluetooth Speaker", "4K Monitor", "Wireless Mouse", "Mechanical Keyboard", "E-Reader", "Action Camera", "Power Bank", "Earbuds"),
            ProductCategory.FASHION to listOf("Denim Jacket", "Leather Boot", "Cotton T-Shirt", "Casual Hoodie", "Sunglasses", "Wool Scarf", "Running Shoes", "Canvas Backpack", "Silk Tie", "Summer Dress"),
            ProductCategory.HOME to listOf("Ergonomic Chair", "Desk Lamp", "Air Purifier", "Espresso Machine", "Throw Blanket", "Ceramic Mug Set", "Robot Vacuum", "Scented Candle", "Blender", "Standing Desk"),
            ProductCategory.BOOKS to listOf("Clean Code", "Design Patterns", "Kotlin in Action", "Refactoring", "System Design", "Domain-Driven Design", "The Pragmatic Programmer", "Algorithms Unlocked", "Soft Skills", "Modern Android"),
            ProductCategory.SPORTS to listOf("Yoga Mat", "Dumbbell Set", "Resistance Bands", "Water Bottle", "Basketball", "Tennis Racket", "Foam Roller", "Jump Rope", "Cycling Gloves", "Running Belt")
        )

        val prefixes = listOf("Pro", "Ultra", "Classic", "Premium", "Compact", "Smart", "Ergonomic", "Portable", "Sleek", "Deluxe")

        (1..200).forEach { id ->
            val category = categories[(id - 1) % categories.size]
            val namesList = baseNames[category] ?: listOf("Product")
            val baseName = namesList[(id - 1) % namesList.size]
            val prefix = prefixes[(id / 2) % prefixes.size]
            val title = "$prefix $baseName #$id"
            val price = 15.0 + ((id * 7) % 280) + 0.99
            val rating = String.format(Locale.US, "%.1f", 3.5 + ((id % 16) / 10.0)).toDouble()
            val stock = (id * 3) % 45 + 5

            val productDto = ProductDto(
                id = id.toString(),
                title = title,
                description = "High quality $title engineered for comfort, durability, and daily use.",
                price = price,
                category = category.name,
                imageUrl = "https://picsum.photos/300/300?random=$id",
                rating = rating,
                stock = stock
            )
            mockProductsMap[id.toString()] = productDto
        }
    }
}

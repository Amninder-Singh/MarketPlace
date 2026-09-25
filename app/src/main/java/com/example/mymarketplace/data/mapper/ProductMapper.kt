package com.example.mymarketplace.data.mapper

import com.example.mymarketplace.data.local.entity.ProductEntity
import com.example.mymarketplace.data.remote.model.ProductDto
import com.example.mymarketplace.domain.model.Product
import com.example.mymarketplace.domain.model.ProductCategory

fun ProductEntity.toDomain(): Product {
    val category = try {
        ProductCategory.valueOf(categoryName)
    } catch (e: Exception) {
        ProductCategory.ALL
    }
    return Product(
        id = id,
        title = title,
        description = description,
        price = price,
        category = category,
        imageUrl = imageUrl,
        rating = rating,
        stock = stock,
        isFavorite = isFavorite,
        pendingImageUpload = pendingImageUpload
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        categoryName = category.name,
        imageUrl = imageUrl,
        rating = rating,
        stock = stock,
        isFavorite = isFavorite,
        pendingImageUpload = pendingImageUpload
    )
}

fun ProductDto.toEntity(isFavorite: Boolean = false, pendingImageUpload: Boolean = false): ProductEntity {
    return ProductEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        categoryName = category,
        imageUrl = imageUrl,
        rating = rating,
        stock = stock,
        isFavorite = isFavorite,
        pendingImageUpload = pendingImageUpload
    )
}

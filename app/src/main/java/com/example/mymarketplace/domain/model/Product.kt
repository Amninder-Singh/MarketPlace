package com.example.mymarketplace.domain.model

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: ProductCategory,
    val imageUrl: String,
    val rating: Double,
    val stock: Int,
    val isFavorite: Boolean = false,
    val pendingImageUpload: Boolean = false
)

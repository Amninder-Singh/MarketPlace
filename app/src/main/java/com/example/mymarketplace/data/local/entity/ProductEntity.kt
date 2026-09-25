package com.example.mymarketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val categoryName: String,
    val imageUrl: String,
    val rating: Double,
    val stock: Int,
    val isFavorite: Boolean = false,
    val pendingImageUpload: Boolean = false
)

package com.example.mymarketplace.data.remote.model

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("stock") val stock: Int
)

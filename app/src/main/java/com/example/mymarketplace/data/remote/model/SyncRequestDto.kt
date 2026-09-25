package com.example.mymarketplace.data.remote.model

import com.google.gson.annotations.SerializedName

data class SyncRequestDto(
    @SerializedName("created") val createdItems: List<ProductDto> = emptyList(),
    @SerializedName("updated") val updatedItems: List<ProductDto> = emptyList()
)

data class SyncResponseDto(
    @SerializedName("syncedListings") val syncedListings: List<ProductDto>,
    @SerializedName("status") val status: String = "SUCCESS",
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

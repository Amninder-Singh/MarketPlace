package com.example.mymarketplace.data.remote.api

import com.example.mymarketplace.data.remote.model.ProductDto
import com.example.mymarketplace.data.remote.model.SyncRequestDto
import com.example.mymarketplace.data.remote.model.SyncResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MarketplaceApi {

    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ProductDto?

    @POST("products")
    suspend fun createProduct(@Body productDto: ProductDto): ProductDto

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body productDto: ProductDto): ProductDto

    @POST("products/sync")
    suspend fun syncProducts(@Body request: SyncRequestDto): SyncResponseDto
}

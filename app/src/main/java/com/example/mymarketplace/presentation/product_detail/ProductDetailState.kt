package com.example.mymarketplace.presentation.product_detail

import com.example.mymarketplace.domain.model.Product

data class ProductDetailState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val showImagePickerSheet: Boolean = false
)

package com.example.mymarketplace.presentation.product_detail

import android.net.Uri

sealed interface ProductDetailEvent {
    data object OnToggleFavorite : ProductDetailEvent
    data class OnImageSelected(val imageUri: Uri) : ProductDetailEvent
    data class OnShowImagePickerOptions(val show: Boolean) : ProductDetailEvent
}

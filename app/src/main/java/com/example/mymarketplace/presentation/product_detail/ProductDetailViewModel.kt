package com.example.mymarketplace.presentation.product_detail

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymarketplace.domain.repository.ProductRepository
import com.example.mymarketplace.domain.usecase.GetProductByIdUseCase
import com.example.mymarketplace.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    getProductByIdUseCase: GetProductByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val productId: String = checkNotNull(savedStateHandle["productId"])
    private val showPickerSheet = MutableStateFlow(false)

    val state: StateFlow<ProductDetailState> = combine(
        getProductByIdUseCase(productId),
        showPickerSheet
    ) { product, showSheet ->
        ProductDetailState(
            product = product,
            isLoading = false,
            showImagePickerSheet = showSheet
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductDetailState(isLoading = true)
    )

    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            ProductDetailEvent.OnToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(productId)
                }
            }
            is ProductDetailEvent.OnShowImagePickerOptions -> {
                showPickerSheet.value = event.show
            }
            is ProductDetailEvent.OnImageSelected -> {
                showPickerSheet.value = false
                saveImageAndUpload(event.imageUri)
            }
        }
    }

    private fun saveImageAndUpload(sourceUri: Uri) {
        viewModelScope.launch {
            val localPath = copyUriToInternalStorage(sourceUri) ?: sourceUri.toString()
            productRepository.updateProductImage(
                productId = productId,
                imageUrl = localPath,
                pendingUpload = true
            )
            productRepository.scheduleImageUpload(
                productId = productId,
                imageUri = localPath
            )
        }
    }

    private suspend fun copyUriToInternalStorage(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val fileName = "product_${productId}_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, fileName)
                val outputStream = FileOutputStream(file)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                Uri.fromFile(file).toString()
            } catch (e: Exception) {
                null
            }
        }
    }
}

package com.example.mymarketplace.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.mymarketplace.data.local.dao.ProductDao
import com.example.mymarketplace.data.mapper.toDomain
import com.example.mymarketplace.data.mapper.toEntity
import com.example.mymarketplace.data.remote.api.MarketplaceApi
import com.example.mymarketplace.data.worker.UploadImageWorker
import com.example.mymarketplace.domain.model.Product
import com.example.mymarketplace.domain.model.ProductCategory
import com.example.mymarketplace.domain.repository.ProductRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productDao: ProductDao,
    private val api: MarketplaceApi
) : ProductRepository {

    private val workManager by lazy { WorkManager.getInstance(context) }

    override fun getProducts(
        category: ProductCategory?,
        searchQuery: String
    ): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities
                .map { it.toDomain() }
                .filter { product ->
                    val matchesCategory = category == null || category == ProductCategory.ALL || product.category == category
                    val matchesSearch = searchQuery.isBlank() ||
                            product.title.contains(searchQuery, ignoreCase = true) ||
                            product.description.contains(searchQuery, ignoreCase = true)
                    matchesCategory && matchesSearch
                }
        }
    }

    override fun getProductById(productId: String): Flow<Product?> {
        return productDao.getProductById(productId).map { it?.toDomain() }
    }

    override fun getFavoriteProducts(): Flow<List<Product>> {
        return productDao.getFavoriteProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavorite(productId: String) {
        productDao.toggleFavorite(productId)
    }

    override suspend fun updateProductImage(
        productId: String,
        imageUrl: String,
        pendingUpload: Boolean
    ) {
        productDao.updateProductImage(productId, imageUrl, pendingUpload)
    }

    override suspend fun updatePendingImageUpload(productId: String, pendingUpload: Boolean) {
        productDao.updatePendingImageUpload(productId, pendingUpload)
    }

    override fun scheduleImageUpload(productId: String, imageUri: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadImageWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    UploadImageWorker.KEY_PRODUCT_ID to productId,
                    UploadImageWorker.KEY_IMAGE_URI to imageUri
                )
            )
            .build()

        workManager.enqueueUniqueWork(
            "UploadImageWork_$productId",
            ExistingWorkPolicy.REPLACE,
            uploadWorkRequest
        )
    }

    override suspend fun refreshProducts() {
        try {
            val remoteDtos = api.getProducts()
            val currentFavorites = productDao.getFavoriteProducts().firstOrNull()?.map { it.id }?.toSet() ?: emptySet()
            val entities = remoteDtos.map { dto ->
                dto.toEntity(isFavorite = currentFavorites.contains(dto.id))
            }
            productDao.insertProducts(entities)
        } catch (_: Exception) {
            // Keep local SSOT data intact if offline/failed network sync
        }
    }
}

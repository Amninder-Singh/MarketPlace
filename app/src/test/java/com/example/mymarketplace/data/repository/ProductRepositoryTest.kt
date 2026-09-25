package com.example.mymarketplace.data.repository

import android.content.Context
import com.example.mymarketplace.data.local.dao.ProductDao
import com.example.mymarketplace.data.local.entity.ProductEntity
import com.example.mymarketplace.data.remote.api.MarketplaceApi
import com.example.mymarketplace.data.remote.model.ProductDto
import com.example.mymarketplace.data.remote.model.SyncRequestDto
import com.example.mymarketplace.data.remote.model.SyncResponseDto
import com.example.mymarketplace.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class FakeProductDao : ProductDao {
    val productsFlow = MutableStateFlow<List<ProductEntity>>(emptyList())

    override fun getAllProducts(): Flow<List<ProductEntity>> = productsFlow
    override fun getProductById(id: String): Flow<ProductEntity?> = MutableStateFlow(productsFlow.value.find { it.id == id })
    override fun getFavoriteProducts(): Flow<List<ProductEntity>> = MutableStateFlow(productsFlow.value.filter { it.isFavorite })
    override suspend fun toggleFavorite(id: String): Int {
        productsFlow.value = productsFlow.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
        return 1
    }
    override suspend fun insertProducts(products: List<ProductEntity>): List<Long> {
        val current = productsFlow.value.toMutableList()
        products.forEach { newProd ->
            val idx = current.indexOfFirst { it.id == newProd.id }
            if (idx >= 0) current[idx] = newProd else current.add(newProd)
        }
        productsFlow.value = current
        return products.map { 1L }
    }
    override suspend fun updateProductImage(id: String, imageUrl: String, pendingUpload: Boolean): Int {
        productsFlow.value = productsFlow.value.map {
            if (it.id == id) it.copy(imageUrl = imageUrl, pendingImageUpload = pendingUpload) else it
        }
        return 1
    }
    override suspend fun updatePendingImageUpload(id: String, pendingUpload: Boolean): Int {
        productsFlow.value = productsFlow.value.map {
            if (it.id == id) it.copy(pendingImageUpload = pendingUpload) else it
        }
        return 1
    }
}

 class FakeApi : MarketplaceApi {
    var remoteList = mutableListOf(
        ProductDto("1", "Remote Phone", "Desc", 499.0, ProductCategory.ELECTRONICS.name, "http://test.com/1.png", 4.5, 10)
    )
    override suspend fun getProducts(): List<ProductDto> = remoteList
    override suspend fun getProductById(id: String): ProductDto? = remoteList.find { it.id == id }
    override suspend fun createProduct(productDto: ProductDto): ProductDto {
        remoteList.add(productDto)
        return productDto
    }
    override suspend fun updateProduct(id: String, productDto: ProductDto): ProductDto {
        val idx = remoteList.indexOfFirst { it.id == id }
        if (idx >= 0) remoteList[idx] = productDto
        return productDto
    }
    override suspend fun syncProducts(request: SyncRequestDto): SyncResponseDto {
        return SyncResponseDto(
            syncedListings = request.createdItems + request.updatedItems,
            status = "SUCCESS"
        )
    }
}

class ProductRepositoryTest {

    private lateinit var mockContext: Context
    private lateinit var fakeDao: FakeProductDao
    private lateinit var fakeApi: FakeApi
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        mockContext = mock(Context::class.java)
        fakeDao = FakeProductDao()
        fakeApi = FakeApi()
        repository = ProductRepositoryImpl(mockContext, fakeDao, fakeApi)
    }

    @Test
    fun refreshProducts_syncsRemoteApiIntoLocalDatabase() = runTest {
        repository.refreshProducts()

        val products = repository.getProducts().first()
        assertEquals(1, products.size)
        assertEquals("Remote Phone", products[0].title)
    }

    @Test
    fun toggleFavorite_updatesLocalPersistence() = runTest {
        fakeDao.insertProducts(listOf(
            ProductEntity("1", "Local Item", "Desc", 99.0, ProductCategory.HOME.name, "url", 4.0, 5, isFavorite = false)
        ))

        repository.toggleFavorite("1")

        val favorites = repository.getFavoriteProducts().first()
        assertEquals(1, favorites.size)
        assertTrue(favorites[0].isFavorite)
    }
}

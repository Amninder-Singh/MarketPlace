package com.example.mymarketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymarketplace.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE isFavorite = 1")
    fun getFavoriteProducts(): Flow<List<ProductEntity>>

    @Query("UPDATE products SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorite(id: String): Int

    @Query("UPDATE products SET imageUrl = :imageUrl, pendingImageUpload = :pendingUpload WHERE id = :id")
    suspend fun updateProductImage(id: String, imageUrl: String, pendingUpload: Boolean): Int

    @Query("UPDATE products SET pendingImageUpload = :pendingUpload WHERE id = :id")
    suspend fun updatePendingImageUpload(id: String, pendingUpload: Boolean): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>): List<Long>
}

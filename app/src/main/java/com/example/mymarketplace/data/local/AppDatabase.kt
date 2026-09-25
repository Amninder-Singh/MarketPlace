package com.example.mymarketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mymarketplace.data.local.dao.ProductDao
import com.example.mymarketplace.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}

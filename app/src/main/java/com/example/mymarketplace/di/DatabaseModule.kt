package com.example.mymarketplace.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mymarketplace.data.local.AppDatabase
import com.example.mymarketplace.data.local.dao.ProductDao
import com.example.mymarketplace.data.local.entity.ProductEntity
import com.example.mymarketplace.domain.model.ProductCategory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        productDaoProvider: Provider<ProductDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "marketplace_db"
        ).fallbackToDestructiveMigration()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    productDaoProvider.get().insertProducts(get200SampleProducts())
                }
            }
        }).build()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    private fun get200SampleProducts(): List<ProductEntity> {
        val categories = arrayOf(
            ProductCategory.ELECTRONICS,
            ProductCategory.FASHION,
            ProductCategory.HOME,
            ProductCategory.BOOKS,
            ProductCategory.SPORTS
        )

        val baseNames = mapOf(
            ProductCategory.ELECTRONICS to listOf("Headphones", "Smart Watch", "Bluetooth Speaker", "4K Monitor", "Wireless Mouse", "Mechanical Keyboard", "E-Reader", "Action Camera", "Power Bank", "Earbuds"),
            ProductCategory.FASHION to listOf("Denim Jacket", "Leather Boot", "Cotton T-Shirt", "Casual Hoodie", "Sunglasses", "Wool Scarf", "Running Shoes", "Canvas Backpack", "Silk Tie", "Summer Dress"),
            ProductCategory.HOME to listOf("Ergonomic Chair", "Desk Lamp", "Air Purifier", "Espresso Machine", "Throw Blanket", "Ceramic Mug Set", "Robot Vacuum", "Scented Candle", "Blender", "Standing Desk"),
            ProductCategory.BOOKS to listOf("Clean Code", "Design Patterns", "Kotlin in Action", "Refactoring", "System Design", "Domain-Driven Design", "The Pragmatic Programmer", "Algorithms Unlocked", "Soft Skills", "Modern Android"),
            ProductCategory.SPORTS to listOf("Yoga Mat", "Dumbbell Set", "Resistance Bands", "Water Bottle", "Basketball", "Tennis Racket", "Foam Roller", "Jump Rope", "Cycling Gloves", "Running Belt")
        )

        val prefixes = listOf("Pro", "Ultra", "Classic", "Premium", "Compact", "Smart", "Ergonomic", "Portable", "Sleek", "Deluxe")

        return (1..200).map { id ->
            val category = categories[(id - 1) % categories.size]
            val namesList = baseNames[category] ?: listOf("Product")
            val baseName = namesList[(id - 1) % namesList.size]
            val prefix = prefixes[(id / 2) % prefixes.size]
            val title = "$prefix $baseName #$id"
            val price = 15.0 + ((id * 7) % 280) + 0.99
            val rating = String.format(Locale.US, "%.1f", 3.5 + ((id % 16) / 10.0)).toDouble()
            val stock = (id * 3) % 45 + 5
            val isFavorite = (id % 7 == 0) || id == 1 || id == 5

            ProductEntity(
                id = id.toString(),
                title = title,
                description = "High quality $title engineered for comfort, durability, and daily use.",
                price = price,
                categoryName = category.name,
                imageUrl = "https://picsum.photos/300/300?random=$id",
                rating = rating,
                stock = stock,
                isFavorite = isFavorite,
                pendingImageUpload = false
            )
        }
    }
}

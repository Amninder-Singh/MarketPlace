package com.example.mymarketplace.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.mymarketplace.data.local.dao.ProductDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class UploadImageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val productDao: ProductDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val productId = inputData.getString(KEY_PRODUCT_ID) ?: return Result.failure()
        val imageUri = inputData.getString(KEY_IMAGE_URI) ?: return Result.failure()

        return try {
            // Simulate network upload to server
            delay(2000)

            // Upon successful upload, clear pending upload flag in DB
            productDao.updatePendingImageUpload(id = productId, pendingUpload = false)

            Result.success(workDataOf(KEY_PRODUCT_ID to productId, KEY_IMAGE_URI to imageUri))
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_PRODUCT_ID = "key_product_id"
        const val KEY_IMAGE_URI = "key_image_uri"
    }
}

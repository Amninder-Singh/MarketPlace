package com.example.mymarketplace.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.mymarketplace.data.worker.SyncProductsWorker
import com.example.mymarketplace.domain.repository.ProductRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class SyncState {
    ONLINE,
    OFFLINE,
    SYNCING
}

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkConnectivityObserver: NetworkConnectivityObserver,
    private val productRepository: ProductRepository
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleBackgroundSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncProductsWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest
        )
    }

    suspend fun refreshDataNow() {
        try {
            productRepository.refreshProducts()
        } catch (_: Exception) {
            scheduleBackgroundSync()
        }
    }

    fun observeSyncState(): Flow<SyncState> {
        val isWorkRunningFlow = workManager.getWorkInfosForUniqueWorkFlow(WORK_NAME)
            .map { infos ->
                infos.any { it.state == WorkInfo.State.RUNNING }
            }

        return combine(
            networkConnectivityObserver.observe(),
            isWorkRunningFlow
        ) { networkStatus, isRunning ->
            when {
                isRunning -> SyncState.SYNCING
                networkStatus == NetworkStatus.UNAVAILABLE -> SyncState.OFFLINE
                else -> SyncState.ONLINE
            }
        }
    }

    companion object {
        const val WORK_NAME = "SyncProductsWork"
    }
}

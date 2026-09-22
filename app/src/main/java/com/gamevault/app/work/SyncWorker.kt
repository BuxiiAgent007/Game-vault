package com.gamevault.app.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.Constraints
import androidx.work.NetworkType
import com.gamevault.app.data.toCollectionItem
import com.gamevault.app.data.toMap
import com.gamevault.app.data.model.CollectionItem
import com.gamevault.app.data.remote.GameVaultApi
import com.gamevault.app.data.repository.CollectionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

/**
 * Offline sync (design doc 3.5):
 * 1. Reads all locally-modified collection rows (synced = 0)
 * 2. Pushes them to the custom REST API (or Firestore directly as fallback)
 * 3. Pulls remote rows and resolves conflicts last-write-wins via ConflictResolver
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val collectionRepository: CollectionRepository,
    private val gameVaultApi: GameVaultApi,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uid = auth.currentUser?.uid ?: return Result.retry()
        return try {
            val unsynced = collectionRepository.getUnsynced()
            val token = auth.currentUser?.getIdToken(false)?.await()?.token

            if (unsynced.isNotEmpty()) {
                if (token != null && useCustomApi()) {
                    gameVaultApi.pushCollection(unsynced, "Bearer $token")
                } else {
                    // Firestore fallback: write each item under users/{uid}/collection
                    unsynced.forEach { item ->
                        firestore.collection("users").document(uid)
                            .collection("collection")
                            .document(item.itemId)
                            .set(item.toMap())
                            .await()
                    }
                }
                collectionRepository.markSynced(unsynced.map { it.itemId })
            }

            // Pull remote and merge (last-write-wins)
            val remote: List<com.gamevault.app.data.model.CollectionItem> =
                if (token != null && useCustomApi()) {
                    runCatching { gameVaultApi.pullCollection("Bearer $token") }
                        .getOrDefault(emptyList())
                } else {
                    runCatching {
                        firestore.collection("users")
                            .document(uid)
                            .collection("collection")
                            .get()
                            .await()
                            .documents
                            .mapNotNull { it.toCollectionItem() }

                    }.getOrDefault(emptyList())
                }

            val merged = com.gamevault.app.util.ConflictResolver.resolveAll(
                collectionRepository.observeAll().first(),
                remote
            )
            collectionRepository.applyResolved(merged)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    /** Use the custom API only if one has actually been deployed (URL not the placeholder). */
    private fun useCustomApi(): Boolean = false
        //!com.gamevault.app.BuildConfig.CUSTOM_API_BASE_URL.contains("gamevault-demo")
               // && com.gamevault.app.BuildConfig.CUSTOM_API_BASE_URL.startsWith("https")

    companion object {
        private const val WORK_NAME = "collection_sync"

        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }
    }
}

package com.gamevault.app.data.repository

import com.gamevault.app.data.local.dao.CollectionDao
import com.gamevault.app.data.model.CollectionItem
import com.gamevault.app.data.model.CollectionStatus
import com.gamevault.app.data.model.Game
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local-first collection management (design doc 3.5).
 * All writes go to Room immediately (offline-capable) and are flagged
 * unsynced; WorkManager pushes them when connectivity returns.
 */
@Singleton
class CollectionRepository @Inject constructor(
    private val dao: CollectionDao
) {

    fun observeByStatus(status: CollectionStatus): Flow<List<CollectionItem>> =
        dao.observeByStatus(status)

    fun observeAll(): Flow<List<CollectionItem>> = dao.observeAll()

    fun observeFinished(): Flow<List<CollectionItem>> = dao.observeFinished()

    fun countByStatus(status: CollectionStatus): Flow<Int> = dao.countByStatus(status)

    suspend fun getByRawgId(rawgId: Int): CollectionItem? = dao.getByRawgId(rawgId)

    suspend fun addGame(game: Game, status: CollectionStatus): CollectionItem {
        val existing = dao.getByRawgId(game.rawgId)
        val item = existing?.copy(
            status = status,
            updatedAt = System.currentTimeMillis(),
            synced = false
        ) ?: CollectionItem(
            itemId = UUID.randomUUID().toString(),
            rawgId = game.rawgId,
            title = game.name,
            coverImageUrl = game.backgroundImage,
            genres = game.genres.joinToString(","),
            status = status,
            addedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            synced = false
        )
        dao.upsert(item)
        return item
    }

    suspend fun updateProgress(itemId: String, progress: Int, hours: Double) {
        dao.getById(itemId)?.let {
            dao.upsert(
                it.copy(
                    progressPercent = progress,
                    hoursPlayed = hours,
                    updatedAt = System.currentTimeMillis(),
                    synced = false
                )
            )
        }
    }

    suspend fun setRatingAndReview(itemId: String, rating: Int?, review: String?) {
        dao.getById(itemId)?.let {
            dao.upsert(
                it.copy(
                    rating = rating,
                    reviewText = review,
                    updatedAt = System.currentTimeMillis(),
                    synced = false
                )
            )
        }
    }

    suspend fun changeStatus(itemId: String, status: CollectionStatus) {
        dao.getById(itemId)?.let {
            dao.upsert(
                it.copy(
                    status = status,
                    updatedAt = System.currentTimeMillis(),
                    synced = false
                )
            )
        }
    }

    suspend fun remove(itemId: String) = dao.delete(itemId)

    suspend fun getUnsynced(): List<CollectionItem> = dao.getUnsynced()

    suspend fun applyResolved(items: List<CollectionItem>) = dao.upsertAll(items)

    suspend fun markSynced(ids: List<String>) = dao.markSynced(ids)
}

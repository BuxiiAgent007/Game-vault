package com.gamevault.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gamevault.app.data.model.CollectionItem
import com.gamevault.app.data.model.CollectionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Query("SELECT * FROM collection_items WHERE status = :status ORDER BY updatedAt DESC")
    fun observeByStatus(status: CollectionStatus): Flow<List<CollectionItem>>

    @Query("SELECT * FROM collection_items")
    fun observeAll(): Flow<List<CollectionItem>>

    @Query("SELECT * FROM collection_items WHERE itemId = :itemId")
    suspend fun getById(itemId: String): CollectionItem?

    @Query("SELECT * FROM collection_items WHERE rawgId = :rawgId")
    suspend fun getByRawgId(rawgId: Int): CollectionItem?

    @Query("SELECT * FROM collection_items WHERE synced = 0")
    suspend fun getUnsynced(): List<CollectionItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CollectionItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CollectionItem>)

    @Query("DELETE FROM collection_items WHERE itemId = :itemId")
    suspend fun delete(itemId: String)

    @Query("UPDATE collection_items SET synced = 1 WHERE itemId IN (:ids)")
    suspend fun markSynced(ids: List<String>)

    @Query("SELECT COUNT(*) FROM collection_items WHERE status = :status")
    fun countByStatus(status: CollectionStatus): Flow<Int>

    @Query("SELECT * FROM collection_items WHERE status = 'FINISHED'")
    fun observeFinished(): Flow<List<CollectionItem>>
}

package com.gamevault.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gamevault.app.data.model.GameCacheEntity

@Dao
interface GameCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(game: GameCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(games: List<GameCacheEntity>)

    @Query("SELECT * FROM game_cache WHERE rawgId = :rawgId")
    suspend fun getById(rawgId: Int): GameCacheEntity?

    @Query("SELECT * FROM game_cache ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<GameCacheEntity>

    @Query("SELECT COUNT(*) FROM game_cache")
    suspend fun count(): Int
}

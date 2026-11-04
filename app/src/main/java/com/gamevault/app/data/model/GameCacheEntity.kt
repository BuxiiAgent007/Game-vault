package com.gamevault.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Offline cache of games fetched from RAWG (24h TTL, design doc 6.4). */
@Entity(tableName = "game_cache")
data class GameCacheEntity(
    @PrimaryKey val rawgId: Int,
    val name: String,
    val backgroundImage: String?,
    val rating: Double?,
    val releaseDate: String?,
    val genres: String,
    val platforms: String,
    val metacritic: Int?,
    val description: String?,
    val screenshots: String,
    val cachedAt: Long = System.currentTimeMillis()
)

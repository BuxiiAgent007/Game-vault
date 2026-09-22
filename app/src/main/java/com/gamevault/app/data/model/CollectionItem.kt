package com.gamevault.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A game in the user's personal library (design doc 6.1/6.3).
 * Local Room DB is the source of truth; WorkManager pushes unsynced rows
 * to Firestore / the custom REST API.
 */
@Entity(tableName = "collection_items")
data class CollectionItem(
    @PrimaryKey val itemId: String,
    val rawgId: Int,
    val title: String,
    val coverImageUrl: String? = null,
    val genres: String = "",
    val status: CollectionStatus,
    val progressPercent: Int = 0,
    val hoursPlayed: Double = 0.0,
    val rating: Int? = null,
    val reviewText: String? = null,
    val addedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)

enum class CollectionStatus { PLAYING, FINISHED, WISHLIST, BACKLOG }
enum class AuthProvider { EMAIL, GOOGLE }
enum class AppLanguage { EN, ZU, TN }
enum class ThemeMode { LIGHT, DARK, SYSTEM }

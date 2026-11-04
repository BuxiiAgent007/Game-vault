package com.gamevault.app.data.model

/** Game catalogue data from the RAWG API (design doc 6.2). */
data class Game(
    val rawgId: Int,
    val name: String,
    val description: String? = null,
    val releaseDate: String? = null,
    val backgroundImage: String? = null,
    val rating: Double? = null,
    val genres: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
    val screenshots: List<String> = emptyList(),
    val metacritic: Int? = null,
    val stores: List<Store> = emptyList()
)

data class Store(val name: String, val url: String)

/** User profile stored in Firestore (design doc 6.1). */
data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String,
    val profileImageUrl: String? = null,
    val authProvider: AuthProvider = AuthProvider.EMAIL,
    val createdAt: Long = 0L,
    val lastLogin: Long = 0L
)

/** Gamification badge (design doc 3.8). */
data class Badge(
    val badgeId: String,
    val userId: String,
    val nameRes: Int,
    val descRes: Int,
    val earnedAt: Long = 0L
)

package com.gamevault.app.data.repository

import com.gamevault.app.R
import com.gamevault.app.data.model.Badge
import com.gamevault.app.data.model.CollectionItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class ProfileStats(
    val finishedThisYear: Int,
    val totalInCollection: Int,
    val hoursLogged: Double,
    val topGenre: String?,
    val rankRes: Int
)

@Singleton
class ProfileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val collectionRepository: CollectionRepository
) {

    val displayName: String
        get() = auth.currentUser?.displayName ?: "Player"

    val email: String
        get() = auth.currentUser?.email ?: ""

    /** Live dashboard stats computed from the local Room library (design doc: visual progress tracking). */
    fun observeStats(): Flow<ProfileStats> = combine(
        collectionRepository.observeAll(),
        collectionRepository.observeFinished()
    ) { all, finished ->
        val yearStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.MONTH, 0); set(java.util.Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
        val finishedYear = finished.count { it.updatedAt >= yearStart || it.addedAt >= yearStart }
        val hours = all.sumOf { it.hoursPlayed }
        val topGenre = finished
            .flatMap { it.genres.split(",").filter { g -> g.isNotBlank() } }
            .groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        val rankRes = when {
            finished.size >= 50 -> R.string.rank_vault_master
            finished.size >= 20 -> R.string.rank_collector
            finished.size >= 5 -> R.string.rank_apprentice
            else -> R.string.rank_newcomer
        }
        ProfileStats(finishedYear, all.size, hours, topGenre, rankRes)
    }

    /** Badges: server-awarded from Firestore, merged with locally earned milestone badges. */
    suspend fun getBadges(): List<Badge> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return runCatching {
            firestore.collection("users").document(uid)
                .collection("badges")
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    val id = doc.getString("badgeId") ?: return@mapNotNull null
                    Badge(
                        badgeId = id,
                        userId = doc.getString("userId") ?: uid,
                        nameRes = (doc.getLong("nameRes") ?: 0L).toInt(),
                        descRes = (doc.getLong("descRes") ?: 0L).toInt(),
                        earnedAt = doc.getLong("earnedAt") ?: 0L
                    )
                }
        }.getOrDefault(emptyList())
    }

    /** Award milestone badges locally when the user hits collection goals (gamification, design doc 3.8). */
    suspend fun awardMilestoneBadges(all: List<CollectionItem>) {
        val uid = auth.currentUser?.uid ?: return
        val finishedCount = all.count { it.status == com.gamevault.app.data.model.CollectionStatus.FINISHED }
        val reviewCount = all.count { it.reviewText != null }
        val toAward = buildList {
            if (finishedCount >= 1) add("first_game" to R.string.badge_first_game_desc)
            if (finishedCount >= 10) add("ten_games" to R.string.badge_ten_games_desc)
            if (reviewCount >= 5) add("reviewer" to R.string.badge_reviewer_desc)
        }
        toAward.forEach { (id, descRes) ->
            runCatching {
                firestore.collection("users").document(uid).collection("badges")
                    .document(id)
                    .set(
                        mapOf(
                            "badgeId" to id,
                            "userId" to uid,
                            "nameRes" to badgeNameRes(id),
                            "descRes" to descRes,
                            "earnedAt" to System.currentTimeMillis()
                        )
                    )
                    .await()
            }
        }
    }

    private fun badgeNameRes(id: String): Int = when (id) {
        "ten_games" -> R.string.badge_ten_games
        "reviewer" -> R.string.badge_reviewer
        else -> R.string.badge_first_game
    }
}

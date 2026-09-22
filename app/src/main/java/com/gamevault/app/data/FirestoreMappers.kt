package com.gamevault.app.data

import com.gamevault.app.data.model.CollectionItem
import com.gamevault.app.data.model.CollectionStatus
import com.google.firebase.firestore.DocumentSnapshot

fun CollectionItem.toMap(): Map<String, Any?> {
    return mapOf(
        "itemId" to itemId,
        "rawgId" to rawgId,
        "title" to title,
        "coverImageUrl" to coverImageUrl,
        "genres" to genres,
        "status" to status.name,
        "progressPercent" to progressPercent,
        "hoursPlayed" to hoursPlayed,
        "rating" to rating,
        "reviewText" to reviewText,
        "addedAt" to addedAt,
        "updatedAt" to updatedAt
    )
}

fun DocumentSnapshot.toCollectionItem(): CollectionItem? {
    val itemId = getString("itemId") ?: id
    val rawgId = getLong("rawgId")?.toInt() ?: return null
    val title = getString("title") ?: return null

    val statusName = getString("status") ?: CollectionStatus.BACKLOG.name

    val status = try {
        CollectionStatus.valueOf(statusName)
    } catch (e: IllegalArgumentException) {
        CollectionStatus.BACKLOG
    }

    return CollectionItem(
        itemId = itemId,
        rawgId = rawgId,
        title = title,
        coverImageUrl = getString("coverImageUrl"),
        genres = getString("genres") ?: "",
        status = status,
        progressPercent = getLong("progressPercent")?.toInt() ?: 0,
        hoursPlayed = getDouble("hoursPlayed") ?: 0.0,
        rating = getLong("rating")?.toInt(),
        reviewText = getString("reviewText"),
        addedAt = getLong("addedAt") ?: System.currentTimeMillis(),
        updatedAt = getLong("updatedAt") ?: System.currentTimeMillis(),
        synced = true
    )
}
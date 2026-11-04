package com.gamevault.app.util

import com.gamevault.app.data.model.CollectionItem

/**
 * Last-write-wins conflict resolution for offline sync (design doc 3.5).
 * The version with the newer [CollectionItem.updatedAt] wins.
 */
object ConflictResolver {

    fun resolve(local: CollectionItem, remote: CollectionItem): CollectionItem {
        val winner = if (remote.updatedAt > local.updatedAt) remote else local
        return winner.copy(synced = true)
    }

    fun resolveAll(
        localItems: List<CollectionItem>,
        remoteItems: List<CollectionItem>
    ): List<CollectionItem> {
        val byId = localItems.associateBy { it.itemId }.toMutableMap()
        for (remote in remoteItems) {
            val local = byId[remote.itemId]
            byId[remote.itemId] = if (local != null) resolve(local, remote) else remote
        }
        return byId.values.toList()
    }
}

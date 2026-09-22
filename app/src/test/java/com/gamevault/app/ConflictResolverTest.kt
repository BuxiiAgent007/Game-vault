package com.gamevault.app

import com.gamevault.app.data.model.CollectionItem
import com.gamevault.app.data.model.CollectionStatus
import com.gamevault.app.util.ConflictResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConflictResolverTest {

    private fun item(id: String, updatedAt: Long, status: CollectionStatus = CollectionStatus.PLAYING) =
        CollectionItem(
            itemId = id,
            rawgId = 1,
            title = "Game",
            status = status,
            updatedAt = updatedAt,
            synced = false
        )

    @Test
    fun `newer remote wins`() {
        val local = item("a", updatedAt = 1000L)
        val remote = item("a", updatedAt = 2000L, status = CollectionStatus.FINISHED)
        val winner = ConflictResolver.resolve(local, remote)
        assertEquals(CollectionStatus.FINISHED, winner.status)
        assertTrue(winner.synced)
    }

    @Test
    fun `newer local wins and is marked synced`() {
        val local = item("a", updatedAt = 3000L, status = CollectionStatus.FINISHED)
        val remote = item("a", updatedAt = 2000L)
        val winner = ConflictResolver.resolve(local, remote)
        assertEquals(CollectionStatus.FINISHED, winner.status)
        assertTrue(winner.synced)
    }

    @Test
    fun `merge keeps union of items`() {
        val local = listOf(item("a", 1000L), item("b", 1000L))
        val remote = listOf(item("b", 5000L, CollectionStatus.FINISHED), item("c", 1000L))
        val merged = ConflictResolver.resolveAll(local, remote)
        assertEquals(3, merged.size)
        assertEquals(CollectionStatus.FINISHED, merged.first { it.itemId == "b" }.status)
    }
}

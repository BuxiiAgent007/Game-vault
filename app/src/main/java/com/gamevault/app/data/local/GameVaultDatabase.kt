package com.gamevault.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gamevault.app.data.local.dao.CollectionDao
import com.gamevault.app.data.local.dao.GameCacheDao
import com.gamevault.app.data.model.CollectionItem
import com.gamevault.app.data.model.GameCacheEntity

@Database(
    entities = [CollectionItem::class, GameCacheEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GameVaultDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
    abstract fun gameCacheDao(): GameCacheDao

    companion object {
        const val NAME = "gamevault.db"
    }
}

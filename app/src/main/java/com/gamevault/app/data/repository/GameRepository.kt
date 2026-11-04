package com.gamevault.app.data.repository

import com.gamevault.app.BuildConfig
import com.gamevault.app.data.local.dao.GameCacheDao
import com.gamevault.app.data.model.Game
import com.gamevault.app.data.model.GameCacheEntity
import com.gamevault.app.data.remote.RawgApi
import com.gamevault.app.data.remote.dto.toGame
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val rawgApi: RawgApi,
    private val cacheDao: GameCacheDao
) {

    private val key: String
        get() = BuildConfig.RAWG_API_KEY.ifBlank {
            throw IllegalStateException("RAWG_API_KEY")
        }

    suspend fun search(
        query: String,
        genre: String? = null,
        platformId: Int? = null,
        year: Int? = null
    ): Result<List<Game>> = runCatching {
        val dates = year?.let { "$it-01-01,$it-12-31" }
        val response = rawgApi.searchGames(
            key = key,
            search = query.ifBlank { null },
            genres = genre,
            platforms = platformId?.toString(),
            dates = dates
        )
        response.results.map { it.toGame() }.also { cacheAll(it) }
    }

    suspend fun getTrending(): Result<List<Game>> = runCatching {
        rawgApi.searchGames(key = key, ordering = "-added")
            .results.map { it.toGame() }.also { cacheAll(it) }
    }

    /** Upcoming releases for the calendar screen (today -> 6 months ahead). */
    suspend fun getUpcoming(): Result<List<Game>> = runCatching {
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val now = java.util.Calendar.getInstance()
        val end = java.util.Calendar.getInstance().apply { add(java.util.Calendar.MONTH, 6) }
        rawgApi.searchGames(
            key = key,
            dates = "${fmt.format(now.time)},${fmt.format(end.time)}",
            ordering = "released"
        ).results.map { it.toGame() }.also { cacheAll(it) }
    }

    suspend fun getDetail(rawgId: Int): Result<Game> = runCatching {
        val detail = rawgApi.getGameDetail(rawgId, key)
        val screenshots = runCatching {
            rawgApi.getScreenshots(rawgId, key).results.map { it.image }
        }.getOrDefault(emptyList())
        detail.toGame(screenshots).also { cacheAll(listOf(it)) }
    }

    /** Personalized recommendations: trending games in the user's top finished genre. */
    suspend fun getRecommendations(topGenre: String?): Result<List<Game>> = runCatching {
        rawgApi.searchGames(
            key = key,
            genres = topGenre ?: "action",
            ordering = "-rating"
        ).results.map { it.toGame() }
    }

    /** Offline fallback: most recently cached games. */
    suspend fun getCached(limit: Int = 20): List<Game> =
        cacheDao.getRecent(limit).map { it.toGame() }

    private suspend fun cacheAll(games: List<Game>) {
        games.forEach { game ->
            val existing = cacheDao.getById(game.rawgId)
            cacheDao.upsert(
                GameCacheEntity(
                    rawgId = game.rawgId,
                    name = game.name,
                    backgroundImage = game.backgroundImage,
                    rating = game.rating,
                    releaseDate = game.releaseDate,
                    genres = game.genres.joinToString(","),
                    platforms = game.platforms.joinToString(","),
                    metacritic = game.metacritic,
                    description = game.description ?: existing?.description,
                    screenshots = game.screenshots
                        .ifEmpty {
                            existing?.screenshots
                                ?.split(",")
                                ?.filter { it.isNotBlank() }
                                .orEmpty()
                        }
                        .joinToString(","),
                    cachedAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun GameCacheEntity.toGame() = Game(
        rawgId = rawgId,
        name = name,
        description = description,
        releaseDate = releaseDate,
        backgroundImage = backgroundImage,
        rating = rating,
        genres = genres.split(",").filter { it.isNotBlank() },
        platforms = platforms.split(",").filter { it.isNotBlank() },
        metacritic = metacritic,
        screenshots = screenshots.split(",").filter { it.isNotBlank() }
    )
}

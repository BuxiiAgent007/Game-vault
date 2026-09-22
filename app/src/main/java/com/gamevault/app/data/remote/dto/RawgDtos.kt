package com.gamevault.app.data.remote.dto

import com.gamevault.app.data.model.Game
import com.google.gson.annotations.SerializedName

data class RawgListResponse<T>(
    val count: Int,
    val results: List<T>
)

data class RawgNamedDto(val name: String)

data class RawgPlatformWrapper(val platform: RawgNamedDto)

data class RawgGameDto(
    val id: Int,
    val name: String,
    @SerializedName("background_image") val backgroundImage: String?,
    val rating: Double?,
    val metacritic: Int?,
    val released: String?,
    val genres: List<RawgNamedDto>?,
    val platforms: List<RawgPlatformWrapper>?
)

data class RawgScreenshotDto(
    val id: Int,
    val image: String
)

data class RawgStoreDto(
    val store: RawgNamedDto,
    @SerializedName("url") val url: String?
)

data class RawgDetailDto(
    val id: Int,
    val name: String,
    @SerializedName("description_raw") val descriptionRaw: String?,
    val released: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    val rating: Double?,
    val metacritic: Int?,
    val genres: List<RawgNamedDto>?,
    val platforms: List<RawgPlatformWrapper>?,
    val stores: List<RawgStoreDto>?
)

fun RawgGameDto.toGame(): Game = Game(
    rawgId = id,
    name = name,
    releaseDate = released,
    backgroundImage = backgroundImage,
    rating = rating,
    metacritic = metacritic,
    genres = genres?.map { it.name } ?: emptyList(),
    platforms = platforms?.map { it.platform.name } ?: emptyList()
)

fun RawgDetailDto.toGame(screenshots: List<String>): Game = Game(
    rawgId = id,
    name = name,
    description = descriptionRaw,
    releaseDate = released,
    backgroundImage = backgroundImage,
    rating = rating,
    metacritic = metacritic,
    genres = genres?.map { it.name } ?: emptyList(),
    platforms = platforms?.map { it.platform.name } ?: emptyList(),
    screenshots = screenshots,
    stores = stores?.mapNotNull { dto ->
        dto.url?.let { com.gamevault.app.data.model.Store(dto.store.name, it) }
    } ?: emptyList()
)

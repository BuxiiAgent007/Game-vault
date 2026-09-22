package com.gamevault.app.data.remote

import com.gamevault.app.data.remote.dto.RawgDetailDto
import com.gamevault.app.data.remote.dto.RawgGameDto
import com.gamevault.app.data.remote.dto.RawgListResponse
import com.gamevault.app.data.remote.dto.RawgScreenshotDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** RAWG Video Games Database API - https://rawg.io/apidocs */
interface RawgApi {

    @GET("games")
    suspend fun searchGames(
        @Query("key") key: String,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("genres") genres: String? = null,
        @Query("platforms") platforms: String? = null,
        @Query("dates") dates: String? = null,
        @Query("ordering") ordering: String? = null
    ): RawgListResponse<RawgGameDto>

    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Int,
        @Query("key") key: String
    ): RawgDetailDto

    @GET("games/{id}/screenshots")
    suspend fun getScreenshots(
        @Path("id") id: Int,
        @Query("key") key: String
    ): RawgListResponse<RawgScreenshotDto>
}

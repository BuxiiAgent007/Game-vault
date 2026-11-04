package com.gamevault.app.data.remote

import com.gamevault.app.data.model.CollectionItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Custom REST API for user-specific data (design doc 3.4).
 * Hosted on Firebase Cloud Functions; the SyncWorker falls back to
 * writing Firestore directly when no custom API is deployed.
 */
interface GameVaultApi {

    @POST("sync/collection")
    suspend fun pushCollection(
        @Body items: List<CollectionItem>,
        @Header("Authorization") token: String
    ): SyncResponse

    @GET("collection")
    suspend fun pullCollection(
        @Header("Authorization") token: String
    ): List<CollectionItem>
}

data class SyncResponse(val synced: Int)

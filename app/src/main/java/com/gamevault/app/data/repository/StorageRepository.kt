package com.gamevault.app.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Storage (blob storage) repository.
 * Profile images are stored as binary objects and only their download URL is
 * saved in the Firestore user document.
 */
@Singleton
class StorageRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    suspend fun uploadProfileImage(uri: Uri): Result<String> = runCatching {
        val user = auth.currentUser ?: error("Please log in before uploading an image")
        Log.d(TAG, "Uploading profile image for user=${user.uid}")

        val reference = storage.reference
            .child("users/${user.uid}/profile/avatar.jpg")

        reference.putFile(uri).await()
        val downloadUrl = reference.downloadUrl.await().toString()

        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(downloadUrl))
                .build()
        ).await()

        firestore.collection("users").document(user.uid)
            .update("profileImageUrl", downloadUrl)
            .await()

        Log.d(TAG, "Profile image uploaded successfully")
        downloadUrl
    }.onFailure {
        Log.e(TAG, "Profile image upload failed", it)
    }

    companion object {
        private const val TAG = "GameVaultStorage"
    }
}

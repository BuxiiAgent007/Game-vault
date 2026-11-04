package com.gamevault.app.data.repository

import com.gamevault.app.data.model.AuthProvider
import com.gamevault.app.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    val currentUser get() = auth.currentUser
    val isLoggedIn get() = auth.currentUser != null

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        updateLastLogin()
    }

    suspend fun register(displayName: String, email: String, password: String): Result<Unit> =
        runCatching {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw IllegalStateException("No user returned")
            user.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(displayName).build()
            ).await()
            createUserDocument(
                UserProfile(
                    userId = user.uid,
                    displayName = displayName,
                    email = email,
                    authProvider = AuthProvider.EMAIL,
                    createdAt = System.currentTimeMillis()
                )
            )
        }

    suspend fun signInWithGoogle(idToken: String): Result<Unit> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("No user returned")
        if (result.additionalUserInfo?.isNewUser == true) {
            createUserDocument(
                UserProfile(
                    userId = user.uid,
                    displayName = user.displayName ?: "Player",
                    email = user.email ?: "",
                    profileImageUrl = user.photoUrl?.toString(),
                    authProvider = AuthProvider.GOOGLE,
                    createdAt = System.currentTimeMillis()
                )
            )
        } else {
            updateLastLogin()
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() = auth.signOut()

    private suspend fun createUserDocument(profile: UserProfile) {
        firestore.collection("users").document(profile.userId)
            .set(
                mapOf(
                    "userId" to profile.userId,
                    "displayName" to profile.displayName,
                    "email" to profile.email,
                    "profileImageUrl" to profile.profileImageUrl,
                    "authProvider" to profile.authProvider.name,
                    "theme" to "SYSTEM",
                    "language" to "EN",
                    "notifRelease" to true,
                    "notifBadge" to true,
                    "createdAt" to profile.createdAt,
                    "lastLogin" to System.currentTimeMillis()
                )
            )
            .await()
    }

    private suspend fun updateLastLogin() {
        val uid = auth.currentUser?.uid ?: return
        runCatching {
            firestore.collection("users").document(uid)
                .update("lastLogin", System.currentTimeMillis())
                .await()
        }
    }
}

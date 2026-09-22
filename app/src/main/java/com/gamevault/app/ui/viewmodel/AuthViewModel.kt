package com.gamevault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamevault.app.data.repository.AuthRepository
import com.gamevault.app.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val authState: StateFlow<UiState<Unit>> = _authState.asStateFlow()

    init {
        // Reflect current session on startup
        _authState.value = if (auth.currentUser != null) {
            UiState.Success(Unit)
        } else {
            UiState.Error("Not logged in")
        }
    }

    fun isLoggedOut(): Boolean = auth.currentUser == null

    // ═══════════════════════════════════════════════════
    //  LOGIN
    // ═══════════════════════════════════════════════════
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepository.login(email, password)
                .onSuccess { _authState.value = UiState.Success(Unit) }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Login failed") }
        }
    }

    // ═══════════════════════════════════════════════════
    //  REGISTER
    // ═══════════════════════════════════════════════════
    fun register(displayName: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepository.register(displayName, email, password)
                .onSuccess { _authState.value = UiState.Success(Unit) }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Registration failed") }
        }
    }

    // ═══════════════════════════════════════════════════
    //  GOOGLE SSO
    // ═══════════════════════════════════════════════════
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepository.signInWithGoogle(idToken)
                .onSuccess { _authState.value = UiState.Success(Unit) }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Google sign-in failed") }
        }
    }

    // ═══════════════════════════════════════════════════
    //  RESET PASSWORD
    // ═══════════════════════════════════════════════════
    fun resetPassword(email: String, onSent: () -> Unit) {
        viewModelScope.launch {
            authRepository.resetPassword(email)
                .onSuccess { onSent() }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Reset failed") }
        }
    }

    // ═══════════════════════════════════════════════════
    //  LOGOUT
    // ═══════════════════════════════════════════════════
    fun logout() {
        authRepository.logout()
        _authState.value = UiState.Error("Not logged in")
    }

    // ═══════════════════════════════════════════════════
    //  DELETE ACCOUNT (design doc §3.3)
    // ═══════════════════════════════════════════════════
    fun deleteAccount() {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                val user = auth.currentUser
                    ?: throw IllegalStateException("No user signed in")

                // 1. Delete Firestore profile doc (best-effort; may fail if rules block it)
                runCatching {
                    firestore.collection("users").document(user.uid)
                        .delete()
                        .await()
                }

                // 2. Delete the Firebase Auth account
                user.delete().await()

                // 3. Signal the UI to navigate to Login
                _authState.value = UiState.Error("ACCOUNT_DELETED")
            } catch (e: Exception) {
                // Common failure: user must re-authenticate before delete.
                // Fall back to logout so the user isn't stuck.
                runCatching { auth.signOut() }
                _authState.value = UiState.Error(
                    "ACCOUNT_DELETED" // still navigate away
                )
            }
        }
    }
}
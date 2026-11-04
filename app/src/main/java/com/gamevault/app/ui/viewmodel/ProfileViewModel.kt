package com.gamevault.app.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamevault.app.R
import com.gamevault.app.data.model.Badge
import com.gamevault.app.data.repository.ProfileRepository
import com.gamevault.app.data.repository.ProfileStats
import com.gamevault.app.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUi(
    val displayName: String,
    val email: String,
    val stats: ProfileStats?,
    val badges: List<Badge>,
    val profileImageUrl: String? = null,
    val uploadingImage: Boolean = false,
    val messageRes: Int? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(
        ProfileUi(
            displayName = profileRepository.displayName,
            email = profileRepository.email,
            profileImageUrl = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.photoUrl?.toString(),
            stats = null,
            badges = emptyList()
        )
    )
    val ui: StateFlow<ProfileUi> = _ui

    val stats: StateFlow<ProfileStats?> = profileRepository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init { loadBadges() }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(uploadingImage = true, messageRes = null)
            storageRepository.uploadProfileImage(uri)
                .onSuccess { url ->
                    Log.d("GameVaultProfile", "Avatar updated")
                    _ui.value = _ui.value.copy(profileImageUrl = url, uploadingImage = false, messageRes = R.string.profile_image_updated)
                }
                .onFailure { error ->
                    Log.e("GameVaultProfile", "Avatar update failed", error)
                    _ui.value = _ui.value.copy(uploadingImage = false, messageRes = R.string.profile_image_upload_failed)
                }
        }
    }

    fun clearMessage() { _ui.value = _ui.value.copy(messageRes = null) }

    fun loadBadges() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(badges = profileRepository.getBadges())
        }
    }
}

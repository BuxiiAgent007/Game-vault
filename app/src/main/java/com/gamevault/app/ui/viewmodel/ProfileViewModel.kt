package com.gamevault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamevault.app.data.model.Badge
import com.gamevault.app.data.repository.ProfileRepository
import com.gamevault.app.data.repository.ProfileStats
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
    val badges: List<Badge>
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(
        ProfileUi(
            displayName = profileRepository.displayName,
            email = profileRepository.email,
            stats = null,
            badges = emptyList()
        )
    )
    val ui: StateFlow<ProfileUi> = _ui

    val stats: StateFlow<ProfileStats?> = profileRepository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init { loadBadges() }

    fun loadBadges() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(badges = profileRepository.getBadges())
        }
    }
}

package com.gamevault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamevault.app.data.model.AppLanguage
import com.gamevault.app.data.model.ThemeMode
import com.gamevault.app.data.repository.AppSettings
import com.gamevault.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(settingsRepository.getSettings())
    val settings: StateFlow<AppSettings> = _settings

    fun setTheme(theme: ThemeMode) = update(_settings.value.copy(theme = theme))
    fun setLanguage(language: AppLanguage) = update(_settings.value.copy(language = language))
    fun setNotifRelease(enabled: Boolean) = update(_settings.value.copy(notifRelease = enabled))
    fun setNotifBadges(enabled: Boolean) = update(_settings.value.copy(notifBadges = enabled))

    private fun update(new: AppSettings) {
        _settings.value = new
        viewModelScope.launch { settingsRepository.saveSettings(new) }
    }
}

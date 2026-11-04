package com.gamevault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamevault.app.data.model.Game
import com.gamevault.app.data.repository.GameRepository
import com.gamevault.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _releases = MutableStateFlow<UiState<List<Game>>>(UiState.Loading)
    val releases: StateFlow<UiState<List<Game>>> = _releases

    init { load() }

    fun load() {
        _releases.value = UiState.Loading
        viewModelScope.launch {
            _releases.value = gameRepository.getUpcoming().fold(
                onSuccess = { UiState.Success(it) },
                onFailure = {
                    val cached = gameRepository.getCached().filter { c ->
                        (c.releaseDate ?: "") >= "2026"
                    }
                    if (cached.isNotEmpty()) UiState.Success(cached)
                    else UiState.Error(it.message ?: "Failed to load calendar")
                }
            )
        }
    }
}

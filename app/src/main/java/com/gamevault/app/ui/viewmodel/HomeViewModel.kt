package com.gamevault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamevault.app.data.model.Game
import com.gamevault.app.data.repository.CollectionRepository
import com.gamevault.app.data.repository.GameRepository
import com.gamevault.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeContent(
    val trending: List<Game>,
    val upcoming: List<Game>,
    val recommended: List<Game>
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow<UiState<HomeContent>>(UiState.Loading)
    val homeState: StateFlow<UiState<HomeContent>> = _homeState

    init { load() }

    fun load() {
        _homeState.value = UiState.Loading
        viewModelScope.launch {
            // Personalization: top genre among finished games drives recommendations
            val finished = collectionRepository.observeFinished().first()
            val topGenre = finished
                .flatMap { it.genres.split(",").filter { g -> g.isNotBlank() } }
                .groupingBy { it.lowercase() }.eachCount()
                .maxByOrNull { it.value }?.key

            val trending = gameRepository.getTrending()
            val upcoming = gameRepository.getUpcoming()
            val recommended = gameRepository.getRecommendations(topGenre)

            val trendingData = trending.getOrElse { gameRepository.getCached() }
            if (trending.isFailure && trendingData.isEmpty()) {
                _homeState.value = UiState.Error(
                    trending.exceptionOrNull()?.message ?: "Failed to load"
                )
                return@launch
            }
            _homeState.value = UiState.Success(
                HomeContent(
                    trending = trendingData,
                    upcoming = upcoming.getOrElse { emptyList() },
                    recommended = recommended.getOrElse { emptyList() }
                )
            )
        }
    }
}

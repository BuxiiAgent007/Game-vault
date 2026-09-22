package com.gamevault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamevault.app.data.model.Game
import com.gamevault.app.data.repository.GameRepository
import com.gamevault.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _results = MutableStateFlow<UiState<List<Game>>>(UiState.Success(emptyList()))
    val results: StateFlow<UiState<List<Game>>> = _results

    val query = MutableStateFlow("")
    val selectedGenre = MutableStateFlow<String?>(null)
    val selectedYear = MutableStateFlow<Int?>(null)

    init {
        viewModelScope.launch {
            query.debounce(300).distinctUntilChanged().collect { q ->
                if (q.isBlank()) {
                    _results.value = UiState.Success(emptyList())
                } else {
                    performSearch(q)
                }
            }
        }
    }

    fun setGenre(genre: String?) { selectedGenre.value = genre; retrigger() }
    fun setYear(year: Int?) { selectedYear.value = year; retrigger() }

    private fun retrigger() {
        val q = query.value
        if (q.isNotBlank()) performSearch(q)
    }

    fun retry() { if (query.value.isNotBlank()) performSearch(query.value) }

    private fun performSearch(q: String) {
        _results.value = UiState.Loading
        viewModelScope.launch {
            val result = gameRepository.search(
                query = q,
                genre = selectedGenre.value,
                year = selectedYear.value
            )
            _results.value = result.fold(
                onSuccess = { games ->
                    if (games.isEmpty()) UiState.Success(emptyList())
                    else UiState.Success(games)
                },
                onFailure = {
                    val cached = gameRepository.getCached().filter { c ->
                        c.name.contains(q, ignoreCase = true)
                    }
                    if (cached.isNotEmpty()) UiState.Success(cached)
                    else UiState.Error(it.message ?: "Search failed")
                }
            )
        }
    }
}

package com.gamevault.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamevault.app.data.model.CollectionItem
import com.gamevault.app.data.model.CollectionStatus
import com.gamevault.app.data.model.Game
import com.gamevault.app.data.repository.CollectionRepository
import com.gamevault.app.data.repository.GameRepository
import com.gamevault.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val rawgId: Int = checkNotNull(savedStateHandle["rawgId"])

    private val _game = MutableStateFlow<UiState<Game>>(UiState.Loading)
    val game: StateFlow<UiState<Game>> = _game

    private val _collectionItem = MutableStateFlow<CollectionItem?>(null)
    val collectionItem: StateFlow<CollectionItem?> = _collectionItem

    init {
        load()
        viewModelScope.launch {
            _collectionItem.value = collectionRepository.getByRawgId(rawgId)
        }
    }

    fun load() {
        _game.value = UiState.Loading
        viewModelScope.launch {
            _game.value = gameRepository.getDetail(rawgId).fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load game") }
            )
        }
    }

    fun addToCollection(status: CollectionStatus) {
        viewModelScope.launch {
            val current = (_game.value as? UiState.Success)?.data ?: return@launch
            collectionRepository.addGame(current, status)
            _collectionItem.value = collectionRepository.getByRawgId(rawgId)
        }
    }
}

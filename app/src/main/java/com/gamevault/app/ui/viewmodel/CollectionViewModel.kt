package com.gamevault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamevault.app.data.local.dao.CollectionDao
import com.gamevault.app.data.model.CollectionItem
import com.gamevault.app.data.model.CollectionStatus
import com.gamevault.app.data.repository.CollectionRepository
import com.gamevault.app.data.repository.ProfileRepository
import com.gamevault.app.work.SyncWorker
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val profileRepository: ProfileRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _status = MutableStateFlow(CollectionStatus.PLAYING)
    val status: StateFlow<CollectionStatus> = _status

    val items: StateFlow<List<CollectionItem>> = _status
        .flatMapLatest { collectionRepository.observeByStatus(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setStatus(status: CollectionStatus) { _status.value = status }

    fun changeStatus(itemId: String, status: CollectionStatus) {
        viewModelScope.launch {
            collectionRepository.changeStatus(itemId, status)
            SyncWorker.enqueue(context)
        }
    }

    fun updateProgress(itemId: String, progress: Int, hours: Double) {
        viewModelScope.launch {
            collectionRepository.updateProgress(itemId, progress, hours)
            SyncWorker.enqueue(context)
        }
    }

    fun setRating(itemId: String, rating: Int?, review: String?) {
        viewModelScope.launch {
            collectionRepository.setRatingAndReview(itemId, rating, review)
            SyncWorker.enqueue(context)
        }
    }

    fun remove(itemId: String) {
        viewModelScope.launch {
            collectionRepository.remove(itemId)
            SyncWorker.enqueue(context)
        }
    }

    init {
        viewModelScope.launch {
            collectionRepository.observeAll().collect { all ->
                profileRepository.awardMilestoneBadges(all)
            }
        }
    }
}

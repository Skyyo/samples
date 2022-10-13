package com.skyyo.samples.features.pagination.simpleWithDatabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.R
import com.skyyo.samples.application.STATE_FLOW_SUB_TIME
import com.skyyo.samples.features.pagination.common.CatsScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_LIMIT = 20

@HiltViewModel
class CatsRoomViewModel @Inject constructor(
    private val catsRepository: CatsRepositoryWithDatabase
) : ViewModel() {

    val events = Channel<CatsScreenEvent>()
    val cats = catsRepository.observeCats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_FLOW_SUB_TIME), listOf())
    val isRefreshing = MutableStateFlow(false)
    val isLastPageReached = MutableStateFlow(false)
    private var isFetchingAllowed = true
    private var itemOffset = 0

    init {
        getCats(true)
    }

    fun onScrollToTopClick() {
        viewModelScope.launch {
            events.send(CatsScreenEvent.ScrollToTop)
        }
    }

    fun onSwipeToRefresh() {
        isLastPageReached.update { false }
        itemOffset = 0
        getCats(true)
    }

    fun getCats(isFirstPage: Boolean = false) {
        if (!isFetchingAllowed) return
        isFetchingAllowed = false
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstPage) isRefreshing.update { true }
            when (val result = catsRepository.getCats(PAGE_LIMIT, itemOffset)) {
                is CatsRoomResult.NetworkError -> {
                    isFetchingAllowed = true
                    events.send(CatsScreenEvent.ShowToast(R.string.network_error))
                }
                is CatsRoomResult.Success -> {
                    itemOffset += PAGE_LIMIT
                    isFetchingAllowed = !result.lastPageReached
                    isLastPageReached.update { result.lastPageReached }
                }
            }
            if (isFirstPage) isRefreshing.update { false }
        }
    }
}

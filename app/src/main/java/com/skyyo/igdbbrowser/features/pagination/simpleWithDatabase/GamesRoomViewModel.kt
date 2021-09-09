package com.skyyo.igdbbrowser.features.pagination.simpleWithDatabase

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.features.pagination.common.GamesEvent
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val PAGE_LIMIT = 20

@HiltViewModel
class GamesRoomViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
    private val gamesRepository: GamesRepositoryWithDatabase
) : ViewModel() {

    val games = gamesRepository.observeGames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5), listOf())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _events = Channel<GamesEvent>()
    val events = _events.receiveAsFlow()

    private var isFetchingAllowed = true
    private var itemOffset = 0
    var isLastPageReached = false

    init {
        getGames(true)
    }

    fun getGames(isFirstPage: Boolean = false) {
        if (!isFetchingAllowed) return
        isFetchingAllowed = false
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstPage) _isRefreshing.value = true
            when (val result = gamesRepository.getGames(PAGE_LIMIT, itemOffset)) {
                is GamesRoomResult.NetworkError -> {
                    isFetchingAllowed = true
                    _events.send(GamesEvent.NetworkError)
                }
                is GamesRoomResult.Success -> {
                    itemOffset += PAGE_LIMIT
                    isFetchingAllowed = !result.lastPageReached
                    isLastPageReached = result.lastPageReached
                }
            }
            if (isFirstPage) _isRefreshing.value = false
        }
    }

    fun onScrollToTopClick() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(GamesEvent.ScrollToTop)
        }
    }

    fun onSwipeToRefresh() {
        isLastPageReached = false
        itemOffset = 0
        getGames(true)
    }
}

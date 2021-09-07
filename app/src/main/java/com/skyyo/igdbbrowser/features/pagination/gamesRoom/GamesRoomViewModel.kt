package com.skyyo.igdbbrowser.features.pagination.gamesRoom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.application.repositories.games.GamesRepository
import com.skyyo.igdbbrowser.application.repositories.games.GamesRoomResult
import com.skyyo.igdbbrowser.features.pagination.GamesEvent
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
    private val gamesRepository: GamesRepository
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
        if (isFirstPage) itemOffset = 0
        isFetchingAllowed = false
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstPage) _isRefreshing.value = true
            when (gamesRepository.getGamesRoom(PAGE_LIMIT, itemOffset)) {
                is GamesRoomResult.NetworkError -> {
                    itemOffset = 0
                    isFetchingAllowed = true
                    _events.send(GamesEvent.NetworkError)
                }
                is GamesRoomResult.Success -> {
                    itemOffset += PAGE_LIMIT
                    isFetchingAllowed = true
                }
                is GamesRoomResult.LastPageReached -> {
                    isLastPageReached = true
                    isFetchingAllowed = false
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

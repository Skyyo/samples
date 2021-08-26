package com.skyyo.igdbbrowser.features.games

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.repositories.games.GamesRepository
import com.skyyo.igdbbrowser.application.repositories.games.GamesResult
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val PAGE_LIMIT = 30

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
    private val gamesRepository: GamesRepository
) : ViewModel() {

    private val _games = MutableStateFlow(listOf<Game>())
    val games = _games.asStateFlow()

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
            when (val result = gamesRepository.getGames(PAGE_LIMIT, itemOffset)) {
                is GamesResult.NetworkError -> {
                    itemOffset = 0
                    isFetchingAllowed = true
                    _events.send(GamesEvent.NetworkError)
                }
                is GamesResult.Success -> {
                    itemOffset += PAGE_LIMIT
                    isFetchingAllowed = true
                    if (isFirstPage) {
                        _games.value = result.games
                    } else {
                        _games.value = (_games.value + result.games)
                    }
                }
                is GamesResult.LastPageReached -> {
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

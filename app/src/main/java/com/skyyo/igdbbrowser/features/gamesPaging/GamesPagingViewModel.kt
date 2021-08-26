package com.skyyo.igdbbrowser.features.gamesPaging

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.pagingSources.GamesSource
import com.skyyo.igdbbrowser.application.repositories.games.GamesRepository
import com.skyyo.igdbbrowser.extensions.log
import com.skyyo.igdbbrowser.features.games.GamesEvent
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val PAGE_LIMIT = 30

@HiltViewModel
class GamesPagingViewModel @Inject constructor(
        private val navigationDispatcher: NavigationDispatcher,
        private val handle: SavedStateHandle,
        private val gamesRepository: GamesRepository
) : ViewModel() {

//    val games = gamesRepository.observeGames()
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5), listOf())


    //TODO difference between flow & stateFlow here
    val games: Flow<PagingData<Game>> = Pager(PagingConfig(
            pageSize = PAGE_LIMIT,
//            maxSize = PAGE_LIMIT * 4
    )) {
        GamesSource(gamesRepository)
    }.flow.cachedIn(viewModelScope)
//    val games: Flow<PagingData<Game>> = gamesPage.flow

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _events = Channel<GamesEvent>()
    val events = _events.receiveAsFlow()


    init {
        //getGames(true)
    }


    fun onScrollToTopClick() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(GamesEvent.ScrollToTop)
        }
    }

    fun onSwipeToRefresh() {
        _isRefreshing.value = true
//         _isRefreshing.value = false
        // getGames(true)
    }
}

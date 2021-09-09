package com.skyyo.igdbbrowser.features.pagination.paging

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.extensions.getStateFlow
import com.skyyo.igdbbrowser.features.pagination.common.GamesEvent
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val PAGE_LIMIT = 30

@HiltViewModel
class GamesPagingViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
    private val gamesRepository: GamesRepositoryPaging
) : ViewModel() {

    //TODO add sample of passing lambda to the GamesSource to listen to events from ViewModel layer?
    // idiotic Paging library design or I've missed something.

    val query = handle.getStateFlow(viewModelScope, "query", "")
    val games: Flow<PagingData<Game>> = query.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = PAGE_LIMIT,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GamesSource(gamesRepository, query) }
        ).flow
    }.cachedIn(viewModelScope)

    private val _events = Channel<GamesEvent>()
    val events = _events.receiveAsFlow()

    fun onScrollToTopClick() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(GamesEvent.ScrollToTop)
        }
    }

    fun onSwipeToRefresh() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(GamesEvent.RefreshList)
        }
    }
}

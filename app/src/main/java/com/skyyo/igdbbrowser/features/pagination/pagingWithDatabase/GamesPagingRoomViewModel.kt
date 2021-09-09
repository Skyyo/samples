package com.skyyo.igdbbrowser.features.pagination.pagingWithDatabase

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.persistance.room.AppDatabase
import com.skyyo.igdbbrowser.application.persistance.room.games.GamesDao
import com.skyyo.igdbbrowser.application.persistance.room.games.GamesRemoteKeysDao
import com.skyyo.igdbbrowser.extensions.getStateFlow
import com.skyyo.igdbbrowser.features.pagination.common.GamesScreenEvent
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val PAGE_LIMIT = 30

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
class GamesPagingRoomViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
    private val db: AppDatabase,
    private val gamesDao: GamesDao, //TODO cleanup the structure with dao/repo/mediator
    private val gamesKeysDao: GamesRemoteKeysDao,
    private val gamesRepo: GamesRepositoryPagingWithDatabase
) : ViewModel() {

    val query = handle.getStateFlow(viewModelScope, "query", "")
    val games: Flow<PagingData<Game>> = query.flatMapLatest { query ->
        Pager(
            config = PagingConfig(pageSize = PAGE_LIMIT, enablePlaceholders = false),
            remoteMediator = GamesRemoteMediator(gamesRepo, db, gamesDao, gamesKeysDao, query),
            pagingSourceFactory = { gamesDao.pagingSource() }
        ).flow
    }.cachedIn(viewModelScope)

    private val _events = Channel<GamesScreenEvent>()
    val events = _events.receiveAsFlow()

    fun onScrollToTopClick() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(GamesScreenEvent.ScrollToTop)
        }
    }

    fun onSwipeToRefresh() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(GamesScreenEvent.RefreshList)
        }
    }

    fun onGamesLoadingError(messageId: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(GamesScreenEvent.ShowToast(messageId))
        }
    }
}

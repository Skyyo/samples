package com.skyyo.igdbbrowser.features.pagination.gamesPagingRoom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.skyyo.igdbbrowser.application.persistance.room.AppDatabase
import com.skyyo.igdbbrowser.application.repositories.games.GamesRepository
import com.skyyo.igdbbrowser.features.pagination.GamesEvent
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val PAGE_LIMIT = 30

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class GamesPagingRoomViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
    private val appDatabase: AppDatabase,
    private val gamesRepository: GamesRepository
) : ViewModel() {


    val gamesDao = appDatabase.gamesDao()
    var query = "wtf"

    val games = Pager(
        config = PagingConfig(pageSize = PAGE_LIMIT),
        remoteMediator = GamesRemoteMediator(appDatabase, gamesRepository)
    ) { gamesDao.pagingSource() }.flow.cachedIn(viewModelScope)


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

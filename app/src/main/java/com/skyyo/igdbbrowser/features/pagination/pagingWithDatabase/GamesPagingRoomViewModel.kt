package com.skyyo.igdbbrowser.features.pagination.pagingWithDatabase

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.skyyo.igdbbrowser.application.persistance.room.GamesDao
import com.skyyo.igdbbrowser.extensions.getStateFlow
import com.skyyo.igdbbrowser.features.pagination.common.GamesScreenEvent
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
    private val gamesDao: GamesDao, //TODO cleanup the structure with dao/repo/mediator
    private val gamesRepository: GamesRepositoryPagingWithDatabase
) : ViewModel() {


    val query = handle.getStateFlow(viewModelScope, "query", "")
    val games = Pager(
        config = PagingConfig(pageSize = PAGE_LIMIT),
        remoteMediator = GamesRemoteMediator(gamesRepository),
        pagingSourceFactory = { gamesDao.pagingSource() }
    ).flow.cachedIn(viewModelScope)

    //region unhidelater
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

    fun onError(messageId: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(GamesScreenEvent.ShowToast(messageId))
        }
    }
    //endregion
}

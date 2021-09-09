package com.skyyo.igdbbrowser.features.pagination.paging

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.extensions.getStateFlow
import com.skyyo.igdbbrowser.features.pagination.common.GamesScreenEvent
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


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GamesPagingViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
    private val gamesRepository: GamesRepositoryPaging
) : ViewModel() {

    //TODO add sample of passing lambda to the GamesSource to listen to events from ViewModel layer?
    // bad Paging library api design forces us to emit events from view layer, or I've missed something.

    val query = handle.getStateFlow(viewModelScope, "query", "")
    val games: Flow<PagingData<Game>> = query
        .flatMapLatest { gamesRepository.getGamesPaging(it) }
        .cachedIn(viewModelScope)

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

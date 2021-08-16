package com.skyyo.igdbbrowser.features.games

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.application.repositories.games.LatestLaunchResult
import com.skyyo.igdbbrowser.application.repositories.games.GamesRepository
import com.skyyo.igdbbrowser.application.repositories.games.PastLaunchesResult
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


private const val PAGE_LIMIT = 20

@HiltViewModel
class LaunchesListViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
    private val gamesRepository: GamesRepository
) : ViewModel() {

//    val latestLaunch = gamesRepository.observeLatestLaunch(LocalDateTime.now(), viewModelScope)
    val pastLaunches = gamesRepository.observePastLaunches(viewModelScope)
    val events = Channel<GamesListEvent>(Channel.UNLIMITED)

    var isFetchingAllowed = true
    private var itemOffset = 0

    init {
       // getLatestLaunch()
        getPastLaunches()
    }

    private fun getLatestLaunch() {
        viewModelScope.launch(Dispatchers.IO) {
//            if (gamesRepository.getLatestLaunch() == LatestLaunchResult.NetworkError) {
//                events.send(GamesListEvent.NetworkError)
//            }
        }
    }

    fun getPastLaunches(isFirstPage: Boolean = false) {
        isFetchingAllowed = false
        if (isFirstPage) itemOffset = 0
        viewModelScope.launch(Dispatchers.IO) {
            when (gamesRepository.getPastLaunches(PAGE_LIMIT, itemOffset)) {
                PastLaunchesResult.NetworkError -> {
                    itemOffset = 0
                    isFetchingAllowed = true
                    events.send(GamesListEvent.NetworkError)
                }
                PastLaunchesResult.Success -> {
                    itemOffset += PAGE_LIMIT
                    isFetchingAllowed = true
                }
                PastLaunchesResult.LastPageReached -> {
                    isFetchingAllowed = false
                }
            }
        }
    }

    fun onSwipeToRefresh() {
        getLatestLaunch()
        getPastLaunches(true)
    }
}

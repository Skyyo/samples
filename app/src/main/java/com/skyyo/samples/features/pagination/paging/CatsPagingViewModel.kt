package com.skyyo.samples.features.pagination.paging

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.features.pagination.common.CatsScreenEvent
import com.skyyo.samples.features.pagination.common.PagingException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CatsPagingViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val catsRepository: CatsRepositoryPaging
) : ViewModel() {

    // TODO add sample of passing lambda to the GamesSource to listen to events from ViewModel layer?
    // bad Paging library api design forces us to emit events from view layer, or I've missed something.

    val events = Channel<CatsScreenEvent>()
    val query = handle.getLiveData("query", "")
    val cats: Flow<PagingData<Cat>> = query.asFlow()
        .flatMapLatest { catsRepository.getCatsPaging(it) }
        .cachedIn(viewModelScope)

    fun onScrollToTopClick() {
        viewModelScope.launch {
            events.send(CatsScreenEvent.ScrollToTop)
        }
    }

    fun onSwipeToRefresh() {
        viewModelScope.launch {
            events.send(CatsScreenEvent.RefreshList)
        }
    }

    fun onCatsLoadingError(exception: PagingException) {
        viewModelScope.launch {
            events.send(CatsScreenEvent.ShowToast(exception.stringRes))
        }
    }
}

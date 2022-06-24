package com.skyyo.samples.features.pagination.pagingWithDatabase

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.extensions.toLazyPagingItems
import com.skyyo.samples.features.pagination.common.CatsScreenEvent
import com.skyyo.samples.features.pagination.common.PagingException
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CatsPagingRoomViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
    private val catsRepository: CatsRepositoryPagingWithDatabase
) : ViewModel() {

    val query = handle.getLiveData("query", "")
    val cats: LazyPagingItems<Cat> = query.asFlow()
        .flatMapLatest { catsRepository.getCatsPaging(it) }
        .cachedIn(viewModelScope).toLazyPagingItems()
    val isRefreshInProgress: State<Boolean>
        get() = derivedStateOf { cats.loadState.refresh is LoadState.Loading }
    val firstPageError: State<Int?>
        get() = derivedStateOf {
            val firstPageError = cats.loadState.refresh as? LoadState.Error
            (firstPageError?.error as PagingException?)?.stringRes
        }
    val currentPageError: State<Int?>
        get() = derivedStateOf {
            val currentPageError = cats.loadState.append as? LoadState.Error
            (currentPageError?.error as PagingException?)?.stringRes
        }
    val newPageLoading: State<Boolean>
        get() = derivedStateOf { cats.loadState.append is LoadState.Loading }

    private val _events = Channel<CatsScreenEvent>()
    val events = _events.receiveAsFlow()

    fun onScrollToTopClick() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(CatsScreenEvent.ScrollToTop)
        }
    }

    fun refresh() {
        cats.refresh()
    }

    fun onRetryClick() {
        cats.retry()
    }
}

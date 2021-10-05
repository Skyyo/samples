package com.skyyo.samples.features.pagination.simple

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.R
import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.features.pagination.common.CatsScreenEvent
import com.skyyo.samples.utils.NavigationDispatcher
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
class CatsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
    private val catsRepository: CatsRepositorySimple
) :
    ViewModel() {

    private val _cats = MutableStateFlow(listOf<Cat>())
    val cats = _cats.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _events = Channel<CatsScreenEvent>()
    val events = _events.receiveAsFlow()

    private var isFetchingAllowed = true
    private var itemOffset = 0
    var isLastPageReached = false

    init {
        getCats(true)
    }

    fun getCats(isFirstPage: Boolean = false) {
        if (!isFetchingAllowed) return
        isFetchingAllowed = false
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstPage) _isRefreshing.value = true
            when (val result = catsRepository.getCats(PAGE_LIMIT, itemOffset)) {
                is CatsResult.NetworkError -> {
                    isFetchingAllowed = true
                    _events.send(CatsScreenEvent.ShowToast(R.string.network_error))
                }
                is CatsResult.Success -> {
                    itemOffset += PAGE_LIMIT
                    isFetchingAllowed = !result.lastPageReached
                    isLastPageReached = result.lastPageReached
                    _cats.value = when {
                        isFirstPage -> result.cats
                        else -> _cats.value + result.cats
                    }
                }
            }
            if (isFirstPage) _isRefreshing.value = false
        }
    }

    fun onScrollToTopClick() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(CatsScreenEvent.ScrollToTop)
        }
    }

    fun onSwipeToRefresh() {
        isLastPageReached = false
        itemOffset = 0
        getCats(true)
    }
}

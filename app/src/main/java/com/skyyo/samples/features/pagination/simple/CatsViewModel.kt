package com.skyyo.samples.features.pagination.simple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.R
import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.features.pagination.common.CatsScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_LIMIT = 30

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val catsRepository: CatsRepositorySimple
) : ViewModel() {

    val events = Channel<CatsScreenEvent>()
    val cats = MutableStateFlow(listOf<Cat>())
    val isRefreshing = MutableStateFlow(false)
    val isLastPageReached = MutableStateFlow(false)
    private var isFetchingAllowed = true
    private var itemOffset = 0

    init {
        getCats(true)
    }

    fun onScrollToTopClick() {
        viewModelScope.launch {
            events.send(CatsScreenEvent.ScrollToTop)
        }
    }

    fun onSwipeToRefresh() {
        isLastPageReached.update { false }
        itemOffset = 0
        getCats(true)
    }

    fun getCats(isFirstPage: Boolean = false) {
        if (!isFetchingAllowed) return
        isFetchingAllowed = false
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstPage) isRefreshing.update { true }
            when (val result = catsRepository.getCats(PAGE_LIMIT, itemOffset)) {
                is CatsResult.NetworkError -> {
                    isFetchingAllowed = true
                    events.send(CatsScreenEvent.ShowToast(R.string.network_error))
                }
                is CatsResult.Success -> {
                    itemOffset += PAGE_LIMIT
                    isFetchingAllowed = !result.lastPageReached
                    isLastPageReached.update { result.lastPageReached }
                    cats.update { catList ->
                        when {
                            isFirstPage -> result.cats
                            else -> catList + result.cats
                        }
                    }
                }
            }
            if (isFirstPage) isRefreshing.update { false }
        }
    }
}

package com.skyyo.samples.features.dominantColor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.features.pagination.paging.CatsRepositoryPaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DominantColorViewModel @Inject constructor(
    private val catsRepositoryPaging: CatsRepositoryPaging,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val query = savedStateHandle.getLiveData("query", "")
    val cats: Flow<PagingData<Cat>> = query.asFlow()
        .flatMapLatest { catsRepositoryPaging.getCatsPaging(it) }
        .cachedIn(viewModelScope)
}
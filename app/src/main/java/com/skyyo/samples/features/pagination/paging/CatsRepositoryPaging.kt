package com.skyyo.samples.features.pagination.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.skyyo.samples.application.network.calls.CatCalls
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

const val PAGE_LIMIT = 30
const val PAGE_INITIAL_LIMIT = 90

@ViewModelScoped
class CatsRepositoryPaging @Inject constructor(private val calls: CatCalls) {

    fun getCatsPaging(query: String) = Pager(
        config = PagingConfig(
            pageSize = PAGE_LIMIT,
            initialLoadSize = PAGE_INITIAL_LIMIT,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { CatsSource(calls, query) }
    ).flow
}
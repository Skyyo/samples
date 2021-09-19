package com.skyyo.samples.features.pagination.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.skyyo.samples.application.network.calls.GamesCalls
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

private const val PAGE_LIMIT = 30

@ViewModelScoped
class GamesRepositoryPaging @Inject constructor(private val calls: GamesCalls) {

    fun getGamesPaging(query: String) = Pager(
        config = PagingConfig(
            pageSize = PAGE_LIMIT,
            initialLoadSize = PAGE_LIMIT,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GamesSource(calls, query) }
    ).flow
}
package com.skyyo.samples.features.pagination.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.skyyo.samples.application.network.calls.GamesCalls
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

// TODO if pageSize is small, not setting initial load size will cause to crash.
// Tested on emulator pixel 3a and motorola nexus 6.
// Repro for pageSize <= 6. For pageSize = 7 repro on nexus 6 with fast scrolling bottom/top.
private const val PAGE_LIMIT = 30

@ViewModelScoped
class GamesRepositoryPaging @Inject constructor(private val calls: GamesCalls) {

    fun getGamesPaging(query: String) = Pager(
        config = PagingConfig(pageSize = PAGE_LIMIT, enablePlaceholders = false),
        pagingSourceFactory = { GamesSource(calls, query) }
    ).flow
}
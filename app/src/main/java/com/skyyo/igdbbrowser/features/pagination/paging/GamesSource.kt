package com.skyyo.igdbbrowser.features.pagination.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.features.pagination.simple.GamesResult

class GamesSource(
    private val repository: GamesRepositoryPaging,
    private val searchQuery: String
) : PagingSource<Int, Game>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        val limit = params.loadSize
        val nextPage: Int
        val offset: Int
        when (params.key) {
            null, 0 -> {
                nextPage = 1
                offset = 0
            }
            else -> {
                nextPage = params.key!!
                offset = nextPage * limit
            }
        }

        return when (val result = repository.getGames(limit, offset)) {
            is GamesResult.Success -> LoadResult.Page(
                data = result.games,
                prevKey = null,
                nextKey = if (result.lastPageReached) null else nextPage.plus(1)
            )
            is GamesResult.NetworkError -> LoadResult.Error(Throwable(R.string.network_error.toString()))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(2)
        }
    }

}

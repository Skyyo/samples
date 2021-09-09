package com.skyyo.igdbbrowser.features.pagination.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.features.pagination.simple.GamesResult

private const val START_PAGE = 0

//TODO kinda makes sense to skip GamesResult and do make the API call here directly. Though
// viewModel will most likely have repository in constructor anyways, so for now we won't separate
// it. Basically GamesSource == UseCase, so it would play well if we use UseCases instead repo one day
class GamesSource(
    private val repository: GamesRepositoryPaging,
    private val query: String
) : PagingSource<Int, Game>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        val limit = params.loadSize
        val page: Int
        val offset: Int
        when (params.key) {
            null -> {
                page = START_PAGE
                offset = 0
            }
            else -> {
                page = params.key!!
                offset = page * limit
            }
        }

        return when (val result = repository.getGames(limit, offset)) {
            is GamesResult.Success -> LoadResult.Page(
                data = result.games,
                prevKey = null,
                nextKey = if (result.lastPageReached) null else page.plus(1)
            )
            is GamesResult.NetworkError -> LoadResult.Error(Throwable(R.string.network_error.toString()))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

package com.skyyo.igdbbrowser.features.pagination.gamesPaging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.repositories.games.GamesRepository
import com.skyyo.igdbbrowser.application.repositories.games.GamesResult

class GamesSource(private val repository: GamesRepository) : PagingSource<Int, Game>() {

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

        return when (val result = repository.getGamesPaging(limit, offset)) {
            is GamesResult.Success -> {
                LoadResult.Page(
                    data = result.games,
                    prevKey = null,
                    nextKey = nextPage.plus(1)
                )
            }
            is GamesResult.NetworkError -> LoadResult.Error(Exception("Network error")) //TODO return R.string.whatever
            is GamesResult.LastPageReached -> LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(2)
        }
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestItemToPosition(anchorPosition)?.id
//        }
//        return state.anchorPosition
    }

}

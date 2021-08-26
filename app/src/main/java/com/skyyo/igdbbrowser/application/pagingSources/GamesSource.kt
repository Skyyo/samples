package com.skyyo.igdbbrowser.application.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.repositories.games.GamesRepository
import com.skyyo.igdbbrowser.application.repositories.games.GamesResult
import com.skyyo.igdbbrowser.extensions.log

class GamesSource(private val repository: GamesRepository) : PagingSource<Int, Game>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        log("page from params ${params.key}")
        val nextPage = if (params.key == null || params.key == 0) 1 else params.key!!
        val limit = params.loadSize
        val offset = nextPage * limit
        return when (val result = repository.getGamesPaging(limit, offset)) {
            is GamesResult.Success -> {
                LoadResult.Page(
                    data = result.games,
                    prevKey = if (nextPage == 1) null else nextPage - 1,
                    nextKey = nextPage.plus(1)
                )
            }
            is GamesResult.NetworkError -> LoadResult.Error(Exception("network error occured"))
            is GamesResult.LastPageReached -> LoadResult.Error(Exception("last page reached"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id
        }
//        return state.anchorPosition
    }
}
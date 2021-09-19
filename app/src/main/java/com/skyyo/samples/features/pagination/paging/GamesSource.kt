package com.skyyo.samples.features.pagination.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyo.samples.R
import com.skyyo.samples.application.models.remote.Game
import com.skyyo.samples.application.network.calls.GamesCalls
import com.skyyo.samples.extensions.log
import com.skyyo.samples.extensions.tryOrNull
import okhttp3.RequestBody.Companion.toRequestBody

private const val START_PAGE = 0

class GamesSource(
    private val gamesCalls: GamesCalls,
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

        val rawBody = "limit $limit; offset $offset;sort id; fields name,first_release_date;"
        log(rawBody)
        val response = tryOrNull { gamesCalls.getGames(rawBody.toRequestBody()) }
        return when {
            response?.code() == 200 -> {
                val games = response.body()!!
                val isLastPageReached = games.size != limit
                LoadResult.Page(
                    data = games,
                    prevKey = null,
                    nextKey = if (isLastPageReached) null else page.plus(1)
                )
            }
            else -> LoadResult.Error(Throwable(R.string.network_error.toString()))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

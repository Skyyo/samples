package com.skyyo.samples.features.pagination.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyo.samples.application.models.remote.Cat
import com.skyyo.samples.application.network.calls.CatCalls
import com.skyyo.samples.extensions.tryOrNull
import com.skyyo.samples.features.pagination.common.PagingException

private const val START_PAGE = 0

class CatsSource(
    private val catCalls: CatCalls,
    private val query: String
) : PagingSource<Int, Cat>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cat> {
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

        val response = tryOrNull { catCalls.getCats(offset, limit) }
        return when {
            response?.code() == 200 -> {
                val cats = response.body()!!
                val isLastPageReached = cats.size != limit
                LoadResult.Page(
                    data = cats,
                    prevKey = null,
                    nextKey = if (isLastPageReached) null else page.plus(1)
                )
            }
            else -> LoadResult.Error(PagingException.NetworkError)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Cat>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

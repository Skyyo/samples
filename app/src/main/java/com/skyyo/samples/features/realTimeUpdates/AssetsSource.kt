package com.skyyo.samples.features.realTimeUpdates

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyo.samples.application.models.Asset
import com.skyyo.samples.application.network.calls.CryptoCalls
import com.skyyo.samples.extensions.tryOrNull
import com.skyyo.samples.features.pagination.common.PagingException
import com.skyyo.samples.features.pagination.paging.PAGE_INITIAL_LIMIT
import com.skyyo.samples.features.pagination.paging.START_PAGE


class AssetsSource(private val calls: CryptoCalls) : PagingSource<Int, Asset>() {
    override fun getRefreshKey(state: PagingState<Int, Asset>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Asset> {
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
                offset = PAGE_INITIAL_LIMIT + (page - 1) * limit
            }
        }
        val response = tryOrNull { calls.getAssets(offset = offset, limit = limit) }
        response ?: return LoadResult.Error(PagingException.NetworkError)
        val assets = response.data
        val isLastPageReached = assets.size != limit
        return LoadResult.Page(
            data = assets,
            prevKey = null,
            nextKey = if (isLastPageReached) null else page.plus(1)
        )
    }
}

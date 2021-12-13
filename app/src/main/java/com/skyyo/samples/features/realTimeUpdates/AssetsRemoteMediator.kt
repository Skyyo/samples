package com.skyyo.samples.features.realTimeUpdates

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.skyyo.samples.application.models.Asset
import com.skyyo.samples.application.network.calls.CryptoCalls
import com.skyyo.samples.application.persistance.room.AppDatabase
import com.skyyo.samples.application.persistance.room.assets.AssetsDao
import com.skyyo.samples.application.persistance.room.assets.AssetsRemoteKeys
import com.skyyo.samples.application.persistance.room.assets.AssetsRemoteKeysDao
import com.skyyo.samples.extensions.tryOrNull
import com.skyyo.samples.features.pagination.pagingWithDatabase.START_PAGE

@OptIn(ExperimentalPagingApi::class)
class AssetsRemoteMediator(
    private val appDatabase: AppDatabase,
    private val calls: CryptoCalls,
    private val assetsDao: AssetsDao,
    private val assetsRemoteKeysDao: AssetsRemoteKeysDao
) : RemoteMediator<Int, Asset>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Asset>): AssetsRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { asset ->
            assetsRemoteKeysDao.remoteKeysById(asset.id.hashCode())
        }
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Asset>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> START_PAGE
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                when (val nextKey = remoteKeys?.nextKey) {
                    null -> {
                        return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                    else -> nextKey
                }
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
        val limit = if (page == START_PAGE) state.config.initialLoadSize else state.config.pageSize
        val offset =
            if (page == START_PAGE) 0 else state.config.initialLoadSize + (page - 1) * limit
        val response = tryOrNull { calls.getAssets(offset = offset, limit = limit) }
        response ?: return MediatorResult.Error(Exception())
        val assets = response.data
        val isLastPageReached = assets.size != limit
        appDatabase.withTransaction {
            if (loadType == LoadType.REFRESH) {
                assetsRemoteKeysDao.deleteRemoteKeys()
                assetsDao.deleteAssets()
            }

            val prevKey = if (page == START_PAGE) null else page - 1
            val nextKey = if (isLastPageReached) null else page + 1

            val keys = assets.map {
                AssetsRemoteKeys(
                    assetKey = it.id.hashCode(),
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            assetsRemoteKeysDao.insertAll(keys)
            assetsDao.insertAll(assets)
        }
        return MediatorResult.Success(endOfPaginationReached = isLastPageReached)
    }
}
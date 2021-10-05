package com.skyyo.samples.features.pagination.pagingWithDatabase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.application.network.calls.CatCalls
import com.skyyo.samples.application.persistance.room.AppDatabase
import com.skyyo.samples.application.persistance.room.cats.CatsDao
import com.skyyo.samples.application.persistance.room.cats.CatsRemoteKeys
import com.skyyo.samples.application.persistance.room.cats.CatsRemoteKeysDao
import com.skyyo.samples.extensions.tryOrNull
import com.skyyo.samples.features.pagination.common.PagingException

private const val START_PAGE = 0

@OptIn(ExperimentalPagingApi::class)
class CatsRemoteMediator(
    private val appDatabase: AppDatabase,
    private val catCalls: CatCalls,
    private val catsDao: CatsDao,
    private val catsKeysDao: CatsRemoteKeysDao,
    private val searchQuery: String
) : RemoteMediator<Int, Cat>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Cat>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                0
//                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
//                remoteKeys?.nextKey?.minus(1) ?: START_PAGE
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                when (val nextKey = remoteKeys?.nextKey) {
                    null -> return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    else -> nextKey
                }
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
//                val remoteKeys = getRemoteKeyForFirstItem(state)
//                // If remoteKeys is null, that means the refresh result is not in the database yet.
//                // We can return Success with `endOfPaginationReached = false` because Paging
//                // will call this method again if RemoteKeys becomes non-null.
//                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
//                // the end of pagination for prepend.
//                when (val prevKey = remoteKeys?.prevKey) {
//                    null -> return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
//                    else -> prevKey
//                }
            }
        }
        val limit = state.config.pageSize
        val offset = if (page == START_PAGE) 0 else page * limit

        val response = tryOrNull { catCalls.getCats(offset, limit) }

        return when {
            response?.code() == 200 -> {
                val cats = response.body()!!
                val isLastPageReached = cats.size != limit
                appDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        catsKeysDao.deleteRemoteKeys()
                        catsDao.deleteCats()
                    }

                    val prevKey = if (page == START_PAGE) null else page - 1
                    val nextKey = if (isLastPageReached) null else page + 1

                    val keys = cats.map {
                        CatsRemoteKeys(catId = it.id.hashCode(), prevKey = prevKey, nextKey = nextKey)
                    }
                    catsKeysDao.insertAll(keys)
                    catsDao.insertAll(cats)
                }
                MediatorResult.Success(endOfPaginationReached = isLastPageReached)
            }
            else -> MediatorResult.Error(PagingException.NetworkError)
        }
    }

    //LoadType.REFRESH
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Cat>
    ): CatsRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                catsKeysDao.remoteKeysCatId(repoId.hashCode())
            }
        }
    }

    //LoadType.APPEND
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Cat>): CatsRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { game ->
            // Get the remote keys of the last item retrieved
            catsKeysDao.remoteKeysCatId(game.id.hashCode())
        }
    }

    //LoadType.PREPEND
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Cat>): CatsRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { game ->
            // Get the remote keys of the first items retrieved
            catsKeysDao.remoteKeysCatId(game.id.hashCode())
        }
    }
}
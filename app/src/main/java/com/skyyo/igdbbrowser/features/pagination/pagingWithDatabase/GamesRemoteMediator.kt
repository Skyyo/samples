package com.skyyo.igdbbrowser.features.pagination.pagingWithDatabase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.persistance.room.AppDatabase
import com.skyyo.igdbbrowser.application.persistance.room.games.GamesDao
import com.skyyo.igdbbrowser.application.persistance.room.games.GamesRemoteKeys
import com.skyyo.igdbbrowser.application.persistance.room.games.GamesRemoteKeysDao
import com.skyyo.igdbbrowser.extensions.log
import com.skyyo.igdbbrowser.features.pagination.simple.GamesResult

private const val START_PAGE = 0

@OptIn(ExperimentalPagingApi::class)
class GamesRemoteMediator(
    private val repository: GamesRepositoryPagingWithDatabase, //TODO cleanup to make as use case?
    private val appDatabase: AppDatabase,
    private val gamesDao: GamesDao,
    private val gamesKeysDao: GamesRemoteKeysDao,
    private val searchQuery: String
) : RemoteMediator<Int, Game>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Game>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                log("REFRESH")
                //TODO 0?
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: START_PAGE
            }
            LoadType.APPEND -> {
                log("APPEND")
                val remoteKeys = getRemoteKeyForLastItem(state)
                when (val nextKey = remoteKeys?.nextKey) {
                    null -> return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    else -> nextKey
                }
            }
            LoadType.PREPEND -> {
                log("PREPEND")
//                return MediatorResult.Success(endOfPaginationReached = true)
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                when (val prevKey = remoteKeys?.prevKey) {
                    null -> return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    else -> prevKey
                }
            }
        }
        val limit = state.config.pageSize
        val offset = if (page == 0) 0 else page * limit
        log("page $page")
        log("offset $offset")
        return when (val result = repository.getGames(limit, offset)) {
            is GamesResult.Success -> {
                appDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        gamesKeysDao.deleteRemoteKeys()
                        gamesDao.deleteGames()
                    }

                    val prevKey = if (page == START_PAGE) null else page - 1
                    val nextKey = if (result.lastPageReached) null else page + 1
                    log("prevKey $prevKey")
                    log("nextKey $nextKey")
                    val keys = result.games.map {
                        GamesRemoteKeys(gameId = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    gamesKeysDao.insertAll(keys)
                    gamesDao.insertAll(result.games)
                }
                MediatorResult.Success(endOfPaginationReached = result.lastPageReached)
            }
            is GamesResult.NetworkError -> MediatorResult.Error(Throwable(R.string.network_error.toString()))
        }
    }

    //LoadType.REFRESH
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Game>
    ): GamesRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                gamesKeysDao.remoteKeysGameId(repoId)
            }
        }
    }

    //LoadType.APPEND
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Game>): GamesRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { game ->
            // Get the remote keys of the last item retrieved
            gamesKeysDao.remoteKeysGameId(game.id)
        }
    }

    //LoadType.PREPEND
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Game>): GamesRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { game ->
            // Get the remote keys of the first items retrieved
            gamesKeysDao.remoteKeysGameId(game.id)
        }
    }
}
package com.skyyo.samples.features.pagination.pagingWithDatabase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.skyyo.samples.R
import com.skyyo.samples.application.models.remote.Game
import com.skyyo.samples.application.network.calls.GamesCalls
import com.skyyo.samples.application.persistance.room.AppDatabase
import com.skyyo.samples.application.persistance.room.games.GamesDao
import com.skyyo.samples.application.persistance.room.games.GamesRemoteKeys
import com.skyyo.samples.application.persistance.room.games.GamesRemoteKeysDao
import com.skyyo.samples.extensions.tryOrNull
import okhttp3.RequestBody.Companion.toRequestBody

private const val START_PAGE = 0

@OptIn(ExperimentalPagingApi::class)
class GamesRemoteMediator(
    private val appDatabase: AppDatabase,
    private val gamesCalls: GamesCalls,
    private val gamesDao: GamesDao,
    private val gamesKeysDao: GamesRemoteKeysDao,
    private val searchQuery: String
) : RemoteMediator<Int, Game>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Game>): MediatorResult {
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

        val rawBody = "limit $limit; offset $offset;sort id; fields name,first_release_date;"
        val response = tryOrNull { gamesCalls.getGames(rawBody.toRequestBody()) }

        return when {
            response?.code() == 200 -> {
                val games = response.body()!!
                val isLastPageReached = games.size != limit
                appDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        gamesKeysDao.deleteRemoteKeys()
                        gamesDao.deleteGames()
                    }

                    val prevKey = if (page == START_PAGE) null else page - 1
                    val nextKey = if (isLastPageReached) null else page + 1

                    val keys = games.map {
                        GamesRemoteKeys(gameId = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    gamesKeysDao.insertAll(keys)
                    gamesDao.insertAll(games)
                }
                MediatorResult.Success(endOfPaginationReached = isLastPageReached)
            }
            else -> MediatorResult.Error(Throwable(R.string.network_error.toString()))
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
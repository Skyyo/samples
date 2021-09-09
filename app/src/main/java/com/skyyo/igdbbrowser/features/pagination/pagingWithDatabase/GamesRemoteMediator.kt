package com.skyyo.igdbbrowser.features.pagination.pagingWithDatabase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.extensions.log
import com.skyyo.igdbbrowser.features.pagination.simple.GamesResult


@OptIn(ExperimentalPagingApi::class)
class GamesRemoteMediator(private val repository: GamesRepositoryPagingWithDatabase) :
    RemoteMediator<Int, Game>() {


    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
//        InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Game>): MediatorResult {
        val nextPage = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                log("append ")
                lastItem.id
            }
        }
        log("anchor pos: ${state.anchorPosition}")
        val limit = state.config.pageSize
        val offset: Int = when (nextPage) {
            null, 0 -> 0
            else -> nextPage * limit
        }

        log("load page $nextPage")

        return when (val result = repository.getGames(limit, offset)) {
            is GamesResult.Success -> {
                when (loadType) {
                    LoadType.REFRESH -> repository.deleteAndInsertGames(result.games)
                    else -> repository.insertGames(result.games)
                }
                MediatorResult.Success(endOfPaginationReached = result.lastPageReached)
            }
            is GamesResult.NetworkError -> MediatorResult.Error(Throwable(R.string.network_error.toString()))
        }
    }
}
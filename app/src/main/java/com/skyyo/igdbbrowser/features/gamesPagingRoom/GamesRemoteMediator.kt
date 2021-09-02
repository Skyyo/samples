package com.skyyo.igdbbrowser.features.gamesPagingRoom

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.persistance.room.AppDatabase
import com.skyyo.igdbbrowser.application.repositories.games.GamesRepository
import com.skyyo.igdbbrowser.extensions.log

sealed class GamesPagingResult {
    class Success(val games: List<Game>, val lastPageReached: Boolean = false) : GamesPagingResult()
    object NetworkError : GamesPagingResult()
}

@OptIn(ExperimentalPagingApi::class)
class GamesRemoteMediator(
    private val database: AppDatabase,
    private val repository: GamesRepository
) : RemoteMediator<Int, Game>() {

    val gamesDao = database.gamesDao()

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

        return when (val result = repository.getGamesPagingRoom(limit, offset)) {
            is GamesPagingResult.Success -> {
                when (loadType) {
                    LoadType.REFRESH -> gamesDao.deleteAndInsertGames(result.games)
                    else -> gamesDao.insertGames(result.games)
                }
                MediatorResult.Success(endOfPaginationReached = result.lastPageReached)
            }
            is GamesPagingResult.NetworkError -> MediatorResult.Error(Exception("network error occurred"))
        }
    }
}
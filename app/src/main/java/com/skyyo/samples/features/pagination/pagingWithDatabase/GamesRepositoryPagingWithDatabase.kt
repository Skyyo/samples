package com.skyyo.samples.features.pagination.pagingWithDatabase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.skyyo.samples.application.network.calls.GamesCalls
import com.skyyo.samples.application.persistance.room.AppDatabase
import com.skyyo.samples.application.persistance.room.games.GamesDao
import com.skyyo.samples.application.persistance.room.games.GamesRemoteKeysDao
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

private const val PAGE_LIMIT = 30

@ViewModelScoped
class GamesRepositoryPagingWithDatabase @Inject constructor(
    private val gameCalls: GamesCalls,
    //TODO need this only for transactions for unrelated DAO's. If needed - use single DAO
    // with @Transaction annotations inside to reduce 3 arguments to 1
    private val db: AppDatabase,
    private val gamesDao: GamesDao,
    private val gamesKeysDao: GamesRemoteKeysDao,
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getGamesPaging(query: String) = Pager(
        config = PagingConfig(
            pageSize = PAGE_LIMIT,
            initialLoadSize = PAGE_LIMIT,
            enablePlaceholders = false
        ),
        remoteMediator = GamesRemoteMediator(db, gameCalls, gamesDao, gamesKeysDao, query),
        pagingSourceFactory = gamesDao::pagingSource
    ).flow

}
package com.skyyo.samples.features.pagination.pagingWithDatabase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.skyyo.samples.application.network.calls.CatCalls
import com.skyyo.samples.application.persistance.room.AppDatabase
import com.skyyo.samples.application.persistance.room.cats.CatsDao
import com.skyyo.samples.application.persistance.room.cats.CatsRemoteKeysDao
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

private const val PAGE_LIMIT = 30

@ViewModelScoped
class CatsRepositoryPagingWithDatabase @Inject constructor(
    private val catsCalls: CatCalls,
    //TODO need this only for transactions for unrelated DAO's. If needed - use single DAO
    // with @Transaction annotations inside to reduce 3 arguments to 1
    private val db: AppDatabase,
    private val catsDao: CatsDao,
    private val catsKeysDao: CatsRemoteKeysDao,
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getCatsPaging(query: String) = Pager(
        config = PagingConfig(
            pageSize = PAGE_LIMIT,
            initialLoadSize = PAGE_LIMIT,
            enablePlaceholders = false
        ),
        remoteMediator = CatsRemoteMediator(db, catsCalls, catsDao, catsKeysDao, query),
        pagingSourceFactory = catsDao::pagingSource
    ).flow

}
package com.skyyo.igdbbrowser.features.gamesPagingRoom

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.network.calls.GamesCalls
import com.skyyo.igdbbrowser.application.persistance.room.AppDatabase

//@OptIn(ExperimentalPagingApi::class)
//class ExampleRemoteMediator(
//    private val query: String,
//    private val database: AppDatabase,
//    private val gamesCalls: GamesCalls
//) : RemoteMediator<Int, Game>() {
//
//    val gamesDao = database.gamesDao()
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, Game>
//    ): MediatorResult {
//
//    }
//}
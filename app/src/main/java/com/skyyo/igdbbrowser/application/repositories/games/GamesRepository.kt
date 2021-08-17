package com.skyyo.igdbbrowser.application.repositories.games

import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.network.calls.GamesCalls
import com.skyyo.igdbbrowser.application.persistance.room.GamesDao
import com.skyyo.igdbbrowser.extensions.tryOrNull
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


@ViewModelScoped
class GamesRepository @Inject constructor(
    private val calls: GamesCalls,
    private val dao: GamesDao
) {

//    fun observeLatestLaunch(currentDate: LocalDateTime, scope: CoroutineScope): StateFlow<Launch?> =
//        dao.observeLatestLaunch(currentDate.toString()).stateIn(scope, WhileSubscribed(4), null)

    //    suspend fun getLatestLaunch(): LatestLaunchResult {
//        return when (val response = tryOrNull { calls.getLatestLaunch() }) {
//            null -> LatestLaunchResult.NetworkError
//            else -> {
//                dao.insertLatestLaunch(response)
//                LatestLaunchResult.Success
//            }
//        }
//    }

    fun observeGames(scope: CoroutineScope): StateFlow<List<Game>> =
        dao.observeGames().stateIn(scope, WhileSubscribed(5), listOf())

    suspend fun getGames(limit: Int, offset: Int): GamesResult {
        val rawBody = "limit $limit; offset $offset; fields name;"
        val response = tryOrNull { calls.getGames(rawBody.toRequestBody()) }
        return when {
            response?.code() == 200 -> {
                val games = response.body()!!
                if (offset == 0) dao.deleteAndInsertGames(games) else dao.insertGames(games)
                if (games.size == limit) GamesResult.SuccessWithoutDatabase(games) else GamesResult.LastPageReached
            }
            else -> GamesResult.NetworkError
        }
    }
}
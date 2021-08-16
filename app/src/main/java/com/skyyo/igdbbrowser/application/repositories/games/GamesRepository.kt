package com.skyyo.igdbbrowser.application.repositories.games

import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.models.remote.Launch
import com.skyyo.igdbbrowser.application.network.calls.GamesCalls
import com.skyyo.igdbbrowser.application.persistance.room.GamesDao
import com.skyyo.igdbbrowser.extensions.tryOrNull
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import javax.inject.Inject


@ViewModelScoped
class GamesRepository @Inject constructor(
    private val calls: GamesCalls,
    private val dao: GamesDao
) {

//    fun observeLatestLaunch(currentDate: LocalDateTime, scope: CoroutineScope): StateFlow<Launch?> =
//        dao.observeLatestLaunch(currentDate.toString()).stateIn(scope, WhileSubscribed(4), null)

    fun observePastLaunches(scope: CoroutineScope): StateFlow<List<Game>> =
        dao.observeGames().stateIn(scope, WhileSubscribed(5), listOf())

//    suspend fun getLatestLaunch(): LatestLaunchResult {
//        return when (val response = tryOrNull { calls.getLatestLaunch() }) {
//            null -> LatestLaunchResult.NetworkError
//            else -> {
//                dao.insertLatestLaunch(response)
//                LatestLaunchResult.Success
//            }
//        }
//    }

    suspend fun getPastLaunches(limit: Int, offset: Int): PastLaunchesResult {
        val requestBody = arrayOf("$limit", "$offset")
        val response = tryOrNull { calls.getGames(requestBody) }
        return when {
            response == null -> PastLaunchesResult.NetworkError
            response.launches.size == limit -> {
             //   dao.insertGames(response.launches)
                PastLaunchesResult.Success
            }
            else -> {
            //    dao.insertGames(response.launches)
                PastLaunchesResult.LastPageReached
            }
        }
    }


}
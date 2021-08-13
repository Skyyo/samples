package com.skyyo.igdbbrowser.application.repositories.launches

import com.skyyo.igdbbrowser.application.models.remote.Launch
import com.skyyo.igdbbrowser.application.network.calls.LaunchesCalls
import com.skyyo.igdbbrowser.application.persistance.room.LaunchesDao
import com.skyyo.igdbbrowser.extensions.tryOrNull
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import javax.inject.Inject


@ViewModelScoped
class LaunchesRepository @Inject constructor(
    private val calls: LaunchesCalls,
    private val dao: LaunchesDao
) {

    fun observeLatestLaunch(currentDate: LocalDateTime, scope: CoroutineScope): StateFlow<Launch?> =
        dao.observeLatestLaunch(currentDate.toString()).stateIn(scope, WhileSubscribed(4), null)

    fun observePastLaunches(scope: CoroutineScope): StateFlow<List<Launch>> =
        dao.observeLatestLaunches().stateIn(scope, WhileSubscribed(4), listOf())

    suspend fun getLatestLaunch(): LatestLaunchResult {
        return when (val response = tryOrNull { calls.getLatestLaunch() }) {
            null -> LatestLaunchResult.NetworkError
            else -> {
                dao.insertLatestLaunch(response)
                LatestLaunchResult.Success
            }
        }
    }

    suspend fun getPastLaunches(limit: Int, offset: Int): PastLaunchesResult {
        val requestBody = arrayOf("$limit", "$offset")
        val response = tryOrNull { calls.getPastLaunches(requestBody) }
        return when {
            response == null -> PastLaunchesResult.NetworkError
            response.launches.size == limit -> {
                dao.insertPastLaunches(response.launches)
                PastLaunchesResult.Success
            }
            else -> {
                dao.insertPastLaunches(response.launches)
                PastLaunchesResult.LastPageReached
            }
        }
    }


}
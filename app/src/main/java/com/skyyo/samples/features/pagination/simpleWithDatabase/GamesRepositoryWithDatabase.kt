package com.skyyo.samples.features.pagination.simpleWithDatabase

import com.skyyo.samples.application.models.remote.Game
import com.skyyo.samples.application.network.calls.GamesCalls
import com.skyyo.samples.application.persistance.room.games.GamesDao
import com.skyyo.samples.extensions.tryOrNull
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


@ViewModelScoped
class GamesRepositoryWithDatabase @Inject constructor(
    private val calls: GamesCalls,
    private val dao: GamesDao
) {

    fun observeGames(): Flow<List<Game>> = dao.observeGames()

    suspend fun getGames(limit: Int, offset: Int): GamesRoomResult {
        val rawBody = "limit $limit; offset $offset;sort id; fields name,first_release_date;"
        val response = tryOrNull { calls.getGames(rawBody.toRequestBody()) }
        return when {
            response?.code() == 200 -> {
                val games = response.body()!!
                if (offset == 0) dao.deleteAndInsertGames(games) else dao.insertGames(games)
                GamesRoomResult.Success(lastPageReached = games.size != limit)
            }
            else -> GamesRoomResult.NetworkError
        }
    }
}
package com.skyyo.igdbbrowser.application.repositories.games

import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.application.network.calls.GamesCalls
import com.skyyo.igdbbrowser.application.persistance.room.GamesDao
import com.skyyo.igdbbrowser.extensions.tryOrNull
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton


//@ViewModelScoped
@Singleton
class GamesRepository @Inject constructor(
    private val calls: GamesCalls,
    private val dao: GamesDao
) {

    fun observeGames(): Flow<List<Game>> = dao.observeGames()

    suspend fun getGames(limit: Int, offset: Int): GamesResult {
        val rawBody = "limit $limit; offset $offset;sort id; fields name,first_release_date;"
        val response = tryOrNull { calls.getGames(rawBody.toRequestBody()) }
        return when {
            response?.code() == 200 -> {
                val games = response.body()!!
                if (games.size == limit) GamesResult.Success(games) else GamesResult.LastPageReached
            }
            else -> GamesResult.NetworkError
        }
    }

    suspend fun getGamesRoom(limit: Int, offset: Int): GamesRoomResult {
        val rawBody = "limit $limit; offset $offset;sort id; fields name,first_release_date;"
        val response = tryOrNull { calls.getGames(rawBody.toRequestBody()) }
        return when {
            response?.code() == 200 -> {
                val games = response.body()!!
                if (offset == 0) dao.deleteAndInsertGames(games) else dao.insertGames(games)
                if (games.size == limit) GamesRoomResult.Success else GamesRoomResult.LastPageReached
            }
            else -> GamesRoomResult.NetworkError
        }
    }
}
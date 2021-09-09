package com.skyyo.igdbbrowser.features.pagination.pagingWithDatabase

import com.skyyo.igdbbrowser.application.network.calls.GamesCalls
import com.skyyo.igdbbrowser.extensions.tryOrNull
import com.skyyo.igdbbrowser.features.pagination.simple.GamesResult
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@ViewModelScoped
class GamesRepositoryPagingWithDatabase @Inject constructor(
    private val calls: GamesCalls,
//    private val gamesDao: GamesDao,
//    private val gamesKeysDao: GamesRemoteKeysDao,
) {

    private var counter = 0

    suspend fun getGames(limit: Int, offset: Int): GamesResult {
        if (limit == 30) counter = 0
        val rawBody = "limit $limit; offset $offset;sort id; fields name,first_release_date;"
        val response = tryOrNull { calls.getGames(rawBody.toRequestBody()) }
        return when {
            response?.code() == 200 -> {
                counter++
                val games = response.body()!!
//                GamesResult.Success(games, games.size != limit)
                GamesResult.Success(games, counter >= 6)
            }
            else -> GamesResult.NetworkError
        }
    }

//    suspend fun deleteAndInsertGames(games: List<Game>) = gamesDao.deleteAndInsertGames(games)

//    suspend fun deleteGames() = gamesDao.deleteGames()

//    suspend fun insertGames(games: List<Game>) = gamesDao.insertGames(games)

//    suspend fun getGameKeyById(gameId: Int) = gamesKeysDao.remoteKeysGameId(gameId)

}
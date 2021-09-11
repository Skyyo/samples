package com.skyyo.igdbbrowser.features.pagination.simple

import com.skyyo.igdbbrowser.application.network.calls.GamesCalls
import com.skyyo.igdbbrowser.extensions.tryOrNull
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@ViewModelScoped
class GamesRepositorySimple @Inject constructor(private val calls: GamesCalls) {

    suspend fun getGames(limit: Int, offset: Int): GamesResult {
        val rawBody = "limit $limit; offset $offset;sort id; fields name,first_release_date;"
        val response = tryOrNull { calls.getGames(rawBody.toRequestBody()) }
        return when {
            response?.code() == 200 -> {
                val games = response.body()!!
                GamesResult.Success(games = games, lastPageReached = games.size != limit)
            }
            else -> GamesResult.NetworkError
        }
    }
}
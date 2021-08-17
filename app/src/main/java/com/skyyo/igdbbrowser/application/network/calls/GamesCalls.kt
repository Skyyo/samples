package com.skyyo.igdbbrowser.application.network.calls

import com.skyyo.igdbbrowser.application.models.remote.Game
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GamesCalls {

    @POST("games")
    suspend fun getGames(@Body body: RequestBody): Response<List<Game>>
}
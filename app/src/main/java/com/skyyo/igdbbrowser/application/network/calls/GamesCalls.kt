package com.skyyo.igdbbrowser.application.network.calls

import com.skyyo.igdbbrowser.application.models.remote.Launch
import com.skyyo.igdbbrowser.application.models.remote.LaunchesWrapper
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GamesCalls {

//    @GET("launches/latest")
//    suspend fun getLatestLaunch(): Launch

//    @GET("launches/past")
//    suspend fun getPastLaunches(
//        @Query("limit") limit: Int,
//        @Query("offset") offset: Int
//    ): List<Launch>

    @POST("launches/query")
    suspend fun getGames(@Body options: Array<String>): LaunchesWrapper
}
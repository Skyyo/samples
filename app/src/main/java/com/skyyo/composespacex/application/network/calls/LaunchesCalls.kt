package com.skyyo.composespacex.application.network.calls

import com.skyyo.composespacex.application.models.remote.Launch
import com.skyyo.composespacex.application.models.remote.LaunchesWrapper
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LaunchesCalls {

    @GET("launches/latest")
    suspend fun getLatestLaunch(): Launch

//    @GET("launches/past")
//    suspend fun getPastLaunches(
//        @Query("limit") limit: Int,
//        @Query("offset") offset: Int
//    ): List<Launch>

    @POST("launches/query")
    suspend fun getPastLaunches(@Body options: Array<String>): LaunchesWrapper
}
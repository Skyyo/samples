package com.skyyo.samples.application.network.calls

import com.skyyo.samples.application.models.Cat
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CatCalls {

    @GET("cats")
    suspend fun getCats(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int,
    ): Response<List<Cat>>
}

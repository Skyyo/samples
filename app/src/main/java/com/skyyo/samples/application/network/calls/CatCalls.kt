package com.skyyo.samples.application.network.calls

import com.skyyo.samples.application.models.Cat
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface CatCalls {

    @GET("cats")
    suspend fun getCats(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int,
    ): Response<List<Cat>>

    @POST
    suspend fun pingVerificationServer(@Url url: String): ResponseBody
}

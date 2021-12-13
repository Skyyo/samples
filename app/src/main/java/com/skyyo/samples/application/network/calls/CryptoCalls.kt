package com.skyyo.samples.application.network.calls

import com.skyyo.samples.application.models.AssetsList
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface CryptoCalls {

    @GET
    suspend fun getAssets(
        @Url url: String = "https://api.coincap.io/v2/assets",
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("search") search: String? = null
    ): AssetsList

}
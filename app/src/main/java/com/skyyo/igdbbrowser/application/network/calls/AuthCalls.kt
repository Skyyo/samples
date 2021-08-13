package com.skyyo.igdbbrowser.application.network.calls

import com.skyyo.igdbbrowser.application.models.remote.TwitchSignInResponse
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface AuthCalls {

    @POST
    suspend fun signIn(
        @Url url: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String,
    ): TwitchSignInResponse

}
package com.skyyo.igdbbrowser.application.network.calls

import com.skyyo.igdbbrowser.application.models.remote.TwitchSignInResponse
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url


private const val TWITCH_OAUTH_URL = "https://id.twitch.tv/oauth2/token"
const val TWITCH_CLIENT_ID = "s9miuod1ccn3x5vwf28n3k2up2mtxy"
private const val TWITCH_CLIENT_SECRET = "uir0le1mwpw1wswqw5sd4mx5l6lm4c"
private const val TWITCH_GRANT_TYPE = "client_credentials"

interface AuthCalls {

    @POST
    suspend fun signIn(
        @Url url: String = TWITCH_OAUTH_URL,
        @Query("client_id") clientId: String = TWITCH_CLIENT_ID,
        @Query("client_secret") clientSecret: String = TWITCH_CLIENT_SECRET,
        @Query("grant_type") grantType: String = TWITCH_GRANT_TYPE,
    ): TwitchSignInResponse

}
package com.skyyo.samples.application.network.calls

import com.skyyo.samples.BuildConfig
import com.skyyo.samples.application.models.OneTapUser
import retrofit2.Response
import retrofit2.http.*

interface OneTapCalls {

    @GET("${BuildConfig.ONE_TAP_URL}/auth/oneTapSignIn")
    suspend fun signIn(@Query("token") token: String): Response<OneTapUser>

    @GET("${BuildConfig.ONE_TAP_URL}/auth/oneTapSignUp")
    suspend fun signUp(@Query("token") token: String): Response<OneTapUser>

    @PUT("${BuildConfig.ONE_TAP_URL}/auth/oneTapUpdateUser")
    suspend fun updateUser(@Body user: OneTapUser): Response<Void>

    @DELETE("${BuildConfig.ONE_TAP_URL}/auth/oneTapDeleteUser")
    suspend fun deleteUser(@Query("userId") userId: String)
}

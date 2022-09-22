package com.skyyo.samples.application.network.calls

import com.skyyo.samples.BuildConfig
import com.skyyo.samples.application.models.OneTapAuthoriseUserRequest
import com.skyyo.samples.application.models.OneTapUser
import retrofit2.Response
import retrofit2.http.*

interface OneTapCalls {

    @POST("${BuildConfig.ONE_TAP_URL}/auth/oneTapAuthorise")
    suspend fun authorise(@Body body: OneTapAuthoriseUserRequest): Response<OneTapUser>

    @PUT("${BuildConfig.ONE_TAP_URL}/auth/oneTapUpdateUser")
    suspend fun updateUser(@Body user: OneTapUser): Response<Void>

    @DELETE("${BuildConfig.ONE_TAP_URL}/auth/oneTapDeleteUser")
    suspend fun deleteUser(@Query("userId") userId: String)
}

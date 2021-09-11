package com.skyyo.samples.application.network

import com.skyyo.samples.application.network.calls.AuthCalls
import com.skyyo.samples.application.persistance.DataStoreManager
import com.skyyo.samples.extensions.tryOrNull
import com.skyyo.samples.utils.eventDispatchers.UnauthorizedEventDispatcher
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitAuthenticator @Inject constructor(
    private val authCalls: Lazy<AuthCalls>,
    private val dataStoreManager: DataStoreManager,
    private val unauthorizedEventDispatcher: UnauthorizedEventDispatcher
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            return runBlocking {
                val requestAccessToken = response.request.header("Authorization")
                val localAccessToken = dataStoreManager.getAccessToken()!!
                if (requestAccessToken != localAccessToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", localAccessToken).build()
                }
                val newAccessToken = getNewAccessToken()
                return@runBlocking if (newAccessToken == null) {
                    unauthorizedEventDispatcher.requestDeauthorization()
                    null
                } else {
                    response.request.newBuilder().header("Authorization", "Bearer $newAccessToken")
                        .build()
                }
            }
        }
    }

    private suspend fun getNewAccessToken(): String? {
        val response = tryOrNull { authCalls.get().signIn() } ?: return null
        dataStoreManager.setAccessToken(response.accessToken)
        return response.accessToken
    }
}

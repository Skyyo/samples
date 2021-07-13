package com.skyyo.composespacex.utils.eventDispatchers

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@ActivityRetainedScoped
class UnauthorizedEventDispatcher @Inject constructor() {
    val unauthorizedEventEmitter = Channel<Boolean>()

    suspend fun requestDeauthorization() {
        unauthorizedEventEmitter.send(true)
    }
}
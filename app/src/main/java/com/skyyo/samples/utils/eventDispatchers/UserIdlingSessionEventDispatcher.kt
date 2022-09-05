package com.skyyo.samples.utils.eventDispatchers

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@ActivityRetainedScoped
class UserIdlingSessionEventDispatcher @Inject constructor() {
    val sessionEventEmitter = Channel<UserIdlingSession>()

    fun stopSession() {
        sessionEventEmitter.trySend(UserIdlingSession.StopSession)
    }

    fun startSession() {
        sessionEventEmitter.trySend(UserIdlingSession.StartSession)
    }
}

package com.skyyo.samples.features.userInteractionTrackingResult

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

enum class IdlingSessionEvent { StartSession, StopSession }

@ActivityRetainedScoped
class UserIdlingSessionEventDispatcher @Inject constructor() {
    val eventEmitter = Channel<IdlingSessionEvent>()

//    fun startSession() = eventEmitter.trySend(IdlingSessionEvent.StartSession)

    fun stopSession() = eventEmitter.trySend(IdlingSessionEvent.StopSession)
}

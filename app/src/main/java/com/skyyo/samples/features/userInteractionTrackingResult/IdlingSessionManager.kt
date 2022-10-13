package com.skyyo.samples.features.userInteractionTrackingResult

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MILLISECONDS_IN_ONE_SECOND = 1_000L
const val SESSION_TIME_IN_SECONDS = 60L
const val SESSION_TIME_IN_MILLISECONDS = MILLISECONDS_IN_ONE_SECOND * SESSION_TIME_IN_SECONDS

@ActivityRetainedScoped
class IdlingSessionManager @Inject constructor(
    val eventDispatcher: UserIdlingSessionEventDispatcher
) {
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    // add debounce to reduce spam
    fun startSession() {
        job?.cancel()
        job = scope.launch {
            delay(SESSION_TIME_IN_MILLISECONDS)
            eventDispatcher.stopSession()
        }
    }

    fun stopSession() {
        job?.cancel()
    }
}

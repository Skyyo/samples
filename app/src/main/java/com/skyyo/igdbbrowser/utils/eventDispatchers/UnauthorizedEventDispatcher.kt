package com.skyyo.igdbbrowser.utils.eventDispatchers

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@ActivityRetainedScoped
class UnauthorizedEventDispatcher @Inject constructor() {
    private val _emitter = Channel<Boolean>()
    val emitter = _emitter.receiveAsFlow()

    suspend fun requestDeauthorization() {
        _emitter.send(true)
    }
}
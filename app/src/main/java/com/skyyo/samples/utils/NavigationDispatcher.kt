package com.skyyo.samples.utils

import androidx.navigation.NavController
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

typealias NavigationEvent = (NavController) -> Unit

@ActivityRetainedScoped
class NavigationDispatcher @Inject constructor() {
    private val _emitter = Channel<NavigationEvent>(Channel.UNLIMITED)
    val emitter = _emitter.receiveAsFlow()

    fun emit(navigationEvent: NavigationEvent) = _emitter.trySend(navigationEvent)

    private val _bottomBarNavControllerEmitter = Channel<NavigationEvent>(Channel.UNLIMITED)
    fun bottomBarNavControllerEmit(navigationEvent: NavigationEvent): ChannelResult<Unit> {
        return _bottomBarNavControllerEmitter.trySend(navigationEvent)
    }
    val bottomBarNavControllerEvents = _bottomBarNavControllerEmitter.receiveAsFlow()

    fun emit(withBottomBar: Boolean?, navigationEvent: NavigationEvent) = when (withBottomBar) {
        true -> bottomBarNavControllerEmit(navigationEvent)
        else -> emit(navigationEvent)
    }
}

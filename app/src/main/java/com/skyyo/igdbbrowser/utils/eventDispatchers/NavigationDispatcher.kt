package com.skyyo.igdbbrowser.utils.eventDispatchers

import androidx.navigation.NavController
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

typealias NavigationCommand = (NavController) -> Unit

@ActivityRetainedScoped
class NavigationDispatcher @Inject constructor() {
    private val _emitter = Channel<NavigationCommand>(Channel.UNLIMITED)
    val emitter = _emitter.receiveAsFlow()

    fun emit(navigationCommand: NavigationCommand) = _emitter.trySend(navigationCommand)


}

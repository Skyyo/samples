package com.skyyo.igdbbrowser.utils.eventDispatchers

import androidx.navigation.NavController
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

typealias NavigationCommand = (NavController) -> Unit

@ActivityRetainedScoped
class NavigationDispatcher @Inject constructor() {
    private val _navigationEmitter = Channel<NavigationCommand>(Channel.UNLIMITED)
    val navigationEmitter = _navigationEmitter.receiveAsFlow()

    fun emit(navigationCommand: NavigationCommand) = _navigationEmitter.trySend(navigationCommand)


}

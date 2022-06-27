package com.skyyo.samples.extensions

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.skyyo.samples.application.Destination
import com.skyyo.samples.utils.NavigationDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun NavigationDispatcher.getBackStackStateHandle(
    destinationId: Int?,
    observer: (SavedStateHandle) -> Unit
) = emit {
    val savedStateHandle = when (destinationId) {
        null -> it.previousBackStackEntry!!.savedStateHandle
        else -> it.getBackStackEntry(destinationId).savedStateHandle
    }
    observer(savedStateHandle)
}

fun NavigationDispatcher.getBackStackStateHandle(
    destinationId: String?,
    observer: (SavedStateHandle) -> Unit
) = emit {
    val savedStateHandle = when (destinationId) {
        null -> it.previousBackStackEntry!!.savedStateHandle
        else -> it.getBackStackEntry(destinationId).savedStateHandle
    }
    observer(savedStateHandle)
}

fun NavController.getBackStackStateHandle(destinationId: String?): SavedStateHandle {
    return when (destinationId) {
        null -> previousBackStackEntry!!.savedStateHandle
        else -> getBackStackEntry(destinationId).savedStateHandle
    }
}

fun <T> NavigationDispatcher.observeNavigationResult(
    coroutineScope: CoroutineScope,
    key: String,
    initialValue: T,
    observer: (T) -> Unit,
) = emit { navController ->
    coroutineScope.launch {
        navController.currentBackStackEntry!!.savedStateHandle.getStateFlow(key, initialValue)
            .collect {
                observer(it)
            }
    }
}

fun NavController.navigateWithObject(
    route: String,
    navOptions: NavOptions? = null,
    extras: Navigator.Extras? = null,
    arguments: Bundle? = null
) {
    val routeLink = NavDeepLinkRequest.Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null && arguments != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, arguments, navOptions, extras)
    } else {
        navigate(route, navOptions, extras)
    }
}

//popUpToRoute - should always be the start destination of the bottomBar, not app
fun NavController.navigateToRootDestination(
    route: String,
    popUpToRoute: String = Destination.Tab1.route
) {
    navigate(route) {
        popUpTo(popUpToRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

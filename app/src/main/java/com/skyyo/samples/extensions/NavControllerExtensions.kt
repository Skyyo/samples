package com.skyyo.samples.extensions

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.skyyo.samples.application.Destination
import com.skyyo.samples.utils.NavigationDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun NavigationDispatcher.getBackStackStateHandle(
    destinationId: Int?,
    withBottomBar: Boolean? = null,
    observer: (SavedStateHandle) -> Unit
) = emit(withBottomBar) {
    val savedStateHandle = when (destinationId) {
        null -> it.previousBackStackEntry!!.savedStateHandle
        else -> it.getBackStackEntry(destinationId).savedStateHandle
    }
    observer(savedStateHandle)
}

fun NavigationDispatcher.getBackStackStateHandle(
    destinationId: String?,
    withBottomBar: Boolean? = null,
    observer: (SavedStateHandle) -> Unit
) = emit(withBottomBar) {
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
    withBottomBar: Boolean? = null,
    observer: (T) -> Unit,
) = emit(withBottomBar) { navController ->
    coroutineScope.launch {
        navController.currentBackStackEntry!!.savedStateHandle.getStateFlow(key, initialValue)
            .collect {
                observer(it)
            }
    }
}

@Suppress("RestrictedApi")
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
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val args = deepLinkMatch.matchingArgs ?: Bundle()
        if (arguments != null) args.putAll(arguments)
        val id = destination.id
        navigate(id, args, navOptions, extras)
    } else {
        navigate(route, navOptions, extras)
    }
}

// popUpToRoute - should always be the start destination of the bottomBar, not app
fun NavController.navigateToRootDestination(
    route: String,
    popInclusive: Boolean = false,
    popUpToRoute: String = Destination.Tab1.route
) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(popUpToRoute) {
            saveState = true
            inclusive = popInclusive
        }
    }
}

@Composable
@SuppressLint("UnrememberedGetBackStackEntry")
fun NavController.rememberBackStackEntry(key: String) = remember(key) {
    getBackStackEntry(key)
}

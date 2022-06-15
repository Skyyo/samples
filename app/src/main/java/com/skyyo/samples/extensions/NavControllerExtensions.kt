package com.skyyo.samples.extensions

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.navigation.*
import com.skyyo.samples.application.Destination
import com.skyyo.samples.utils.NavigationDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//sets value to previous savedStateHandle unless route is specified
fun <T> NavController.setNavigationResult(route: String? = null, key: String, result: T) {
    if (route == null) {
        previousBackStackEntry?.savedStateHandle?.set(key, result)
    } else {
        getBackStackEntry(route).savedStateHandle.set(key, result)
    }
}

fun <T> NavController.getNavigationResult(key: String) =
    currentBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> NavController.observeNavigationResultLiveData(key: String) =
    currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> NavController.observeNavigationResult(key: String) =
    currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)?.asFlow()

fun NavigationDispatcher.observeBackStackStateHandle(
    destinationId: Int?,
    observer: (SavedStateHandle) -> Unit
) = emit {
    val savedStateHandle = when(destinationId) {
        null -> it.previousBackStackEntry!!.savedStateHandle
        else -> it.getBackStackEntry(destinationId).savedStateHandle
    }
    observer(savedStateHandle)
}

fun <T> NavigationDispatcher.observeNavigationResult(
    coroutineScope: CoroutineScope,
    key: String,
    initialValue: T,
    observer: (T) -> Unit,
) = emit { navController ->
    coroutineScope.launch {
        navController.currentBackStackEntry!!.savedStateHandle.getStateFlow(key, initialValue).collect {
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

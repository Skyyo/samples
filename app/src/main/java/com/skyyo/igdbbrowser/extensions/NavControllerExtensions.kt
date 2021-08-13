package com.skyyo.igdbbrowser.extensions

import androidx.lifecycle.asFlow
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

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

fun NavController.navigateToRootDestination(route: String) {
//    if (route == currentDestination?.route) return
    //TODO add check for root destinations instead
    navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}


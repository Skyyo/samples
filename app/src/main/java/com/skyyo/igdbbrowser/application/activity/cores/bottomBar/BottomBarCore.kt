package com.skyyo.igdbbrowser.application.activity.cores.bottomBar

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.activity.PopulatedNavHost
import com.skyyo.igdbbrowser.extensions.log
import com.skyyo.igdbbrowser.extensions.navigateToRootDestination
import com.skyyo.igdbbrowser.theme.IgdbBrowserTheme
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
@ExperimentalAnimationApi
fun BottomBarCore(
    navigationDispatcher: NavigationDispatcher,
    bottomBarScreens: List<Screens>,
    startDestination: String
) {
    IgdbBrowserTheme {
        //TODO move navController & dispatchers either to top, or further down, measure.
        val navController = rememberNavController()
        Surface(color = MaterialTheme.colors.background) {
            val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
            val selectedTab = rememberSaveable { mutableStateOf(0) }

            DisposableEffect(navController) {
                val callback = NavController.OnDestinationChangedListener { _, destination, args ->
                    log("${destination.route}")
                    when (destination.route) {
                        Screens.AuthScreen.route -> isBottomBarVisible.value = false
                        else -> isBottomBarVisible.value = true
                    }
                }
                navController.addOnDestinationChangedListener(callback)
                onDispose {
                    navController.removeOnDestinationChangedListener(callback)
                }
            }

            Scaffold(
                bottomBar = {
                    AnimatedBottomBar(
                        bottomBarScreens,
                        selectedTab.value,
                        isBottomBarVisible.value
                    ) { index, route ->
                        selectedTab.value = index
                        navController.navigateToRootDestination(route)
                    }
                },
                content = { innerPadding ->
                    PopulatedNavHost(
                        startDestination = startDestination,
                        innerPadding = innerPadding,
                        navController = navController,
                        onBackPressIntercepted = {
                            selectedTab.value = 0
                            navController.navigateToRootDestination(Screens.DogFeedScreen.route)
                        }
                    )
                })
        }
        //region dispatchers
        val lifecycleOwner = LocalLifecycleOwner.current
        //TODO might be risky to drop flow ?
        val lifecycleAwareNavigationCommandsFlow =
            remember(navigationDispatcher.navigationEmitter, lifecycleOwner) {
                navigationDispatcher.navigationEmitter.flowWithLifecycle(
                    lifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
            }
        //TODO disposable effect?
        LaunchedEffect(Unit) {
            launch {
                lifecycleAwareNavigationCommandsFlow.collect { command ->
                    command(navController)
                }
            }
        }
        //endregion
    }
}
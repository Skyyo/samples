package com.skyyo.composespacex.application.activity.cores.drawer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.application.activity.PopulatedNavHost
import com.skyyo.composespacex.extensions.log
import com.skyyo.composespacex.extensions.navigateToRootDestination
import com.skyyo.composespacex.theme.ComposeSpaceXTheme
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
@ExperimentalAnimationApi
fun DrawerCore(
    navigationDispatcher: NavigationDispatcher,
    drawerScreens: List<Screens>,
    startDestination: String
) {
    ComposeSpaceXTheme {
        //TODO move navController & dispatchers either to top, or further down, measure.
        val navController = rememberNavController()
        Surface(color = MaterialTheme.colors.background) {
            val isDrawerVisible = rememberSaveable { mutableStateOf(false) }
            val selectedTab = rememberSaveable { mutableStateOf(0) }
            val scaffoldState = rememberScaffoldState()
            val scope = rememberCoroutineScope()

            DisposableEffect(navController) {
                val callback = NavController.OnDestinationChangedListener { _, destination, args ->
                    log("${destination.route}")
                    when (destination.route) {
                        Screens.AuthScreen.route -> isDrawerVisible.value = false
                        else -> isDrawerVisible.value = true
                    }
                }
                navController.addOnDestinationChangedListener(callback)
                onDispose {
                    navController.removeOnDestinationChangedListener(callback)
                }
            }

            Scaffold(
                scaffoldState = scaffoldState,
                drawerGesturesEnabled = isDrawerVisible.value,
                drawerContent = {
                    if (isDrawerVisible.value) {
                        Drawer(
                            screens = drawerScreens,
                            selectedTab = selectedTab.value
                        ) { index, route ->
                            selectedTab.value = index
                            navController.navigateToRootDestination(route)
                            scope.launch { scaffoldState.drawerState.close() }//TODO possible optimization
                        }
                    }
                },
                content = { innerPadding ->
                    PopulatedNavHost(
                        startDestination = startDestination,
                        innerPadding = innerPadding,
                        navController = navController,
                        //TODO is this needed with drawer?
                        onBackPressIntercepted = {
                            selectedTab.value = 0
                            navController.navigateToRootDestination(Screens.DogFeedScreen.route)
                        }
                    )
                }
            )
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



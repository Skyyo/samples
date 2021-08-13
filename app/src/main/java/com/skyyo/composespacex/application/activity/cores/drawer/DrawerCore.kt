package com.skyyo.composespacex.application.activity.cores.drawer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
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
                //this allows to dismiss the drawer if its open, by tapping on dimmed area
                drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                floatingActionButton = {
                    if (isDrawerVisible.value) {
                        FloatingActionButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                            Text(text = "open drawer")
                        }
                    }
                },
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



package com.skyyo.composespacex.application.activity.cores.drawer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.application.activity.PopulatedNavHost
import com.skyyo.composespacex.application.persistance.DataStoreManager
import com.skyyo.composespacex.extensions.log
import com.skyyo.composespacex.extensions.navigateToBottomNavDestination
import com.skyyo.composespacex.theme.ComposeSpaceXTheme
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
@ExperimentalAnimationApi
fun DrawerCore(
    navigationDispatcher: NavigationDispatcher,
    dataStoreManager: DataStoreManager
) {
    ComposeSpaceXTheme {
        val navController = rememberNavController()
        Surface(color = MaterialTheme.colors.background) {
            val isDrawerVisible = rememberSaveable { mutableStateOf(false) }
            val selectedTab = rememberSaveable { mutableStateOf(0) }
            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
            val scope = rememberCoroutineScope()
            val drawerScreens = listOf(
                Screens.DogFeedScreen,
                Screens.Profile,
                Screens.UpcomingLaunches,
                Screens.SamplesScreen,
            )
            val startDestination = when {
                //TODO measure async + splash delegation profit
                runBlocking { dataStoreManager.getAccessToken() } == null -> Screens.AuthScreen.route
                else -> Screens.DogFeedScreen.route
            }

            // TODO issue with destination being navigated to multiple times!

//            val navBackStackEntry by navController.currentBackStackEntryAsState()
//            val currentRoute = navBackStackEntry?.destination?.route
//            log("$currentRoute")
//            when (currentRoute) {
//                Screens.AuthScreen.route -> isDrawerVisible.value = false
//                else -> isDrawerVisible.value = true
//            }
            navController.addOnDestinationChangedListener { _, destination, args ->
                log("${destination.route}")
                when (destination.route) {
                    Screens.AuthScreen.route -> isDrawerVisible.value = false
                    else -> isDrawerVisible.value = true
                }
            }
            Scaffold(
                scaffoldState = scaffoldState,
                drawerGesturesEnabled = isDrawerVisible.value, //TODO explore, there is issue with this invoking addOnDestinationChangedListener when set to remembered value
                drawerContent = {
                    if (isDrawerVisible.value) {
                        Drawer(
                            screens = drawerScreens,
                            selectedTab = selectedTab.value
                        ) { index, route ->
                            selectedTab.value = index
                            navController.navigateToBottomNavDestination(route)
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
                            navController.navigateToBottomNavDestination(Screens.DogFeedScreen.route)
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


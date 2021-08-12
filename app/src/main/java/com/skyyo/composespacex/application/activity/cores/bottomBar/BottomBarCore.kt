package com.skyyo.composespacex.application.activity.cores.bottomBar

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun BottomBarCore(navigationDispatcher: NavigationDispatcher, dataStoreManager: DataStoreManager) {
    ComposeSpaceXTheme {
        val navController = rememberNavController()
        Surface(color = MaterialTheme.colors.background) {
            val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
            val selectedTab = rememberSaveable { mutableStateOf(0) }
            val bottomNavScreens = listOf(
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
            //TODO compare with navController.currentBackStackEntryAsState()
            navController.addOnDestinationChangedListener { _, destination, args ->
                log("${destination.route}")
                when (destination.route) {
                    Screens.AuthScreen.route -> isBottomBarVisible.value = false
                    else -> isBottomBarVisible.value = true
                }
            }
            Scaffold(
                bottomBar = {
                    AnimatedBottomBar(
                        bottomNavScreens,
                        selectedTab.value,
                        isBottomBarVisible.value
                    ) { index, route ->
                        selectedTab.value = index
                        navController.navigateToBottomNavDestination(route)
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
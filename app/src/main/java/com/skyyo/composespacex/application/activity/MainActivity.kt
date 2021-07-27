package com.skyyo.composespacex.application.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.application.persistance.DataStoreManager
import com.skyyo.composespacex.extensions.navigateToBottomNavDestination
import com.skyyo.composespacex.theme.ComposeSpaceXTheme
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

const val EXPAND_ANIMATION_DURATION = 300
const val COLLAPSE_ANIMATION_DURATION = 300
const val FADE_IN_ANIMATION_DURATION = 350
const val FADE_OUT_ANIMATION_DURATION = 300

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSpaceXTheme {
                //TODO might be risky to drop flow ?
                val lifecycleOwner = LocalLifecycleOwner.current
                val lifecycleAwareNavigationCommandsFlow =
                    remember(navigationDispatcher.navigationEmitter, lifecycleOwner) {
                        navigationDispatcher.navigationEmitter.flowWithLifecycle(
                            lifecycleOwner.lifecycle,
                            Lifecycle.State.STARTED
                        )
                    }
                val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
                val bottomNavScreens = listOf(
                    Screens.DogFeedScreen,
                    Screens.Profile,
                    Screens.FriendsList,
                )
                val startDestination = when {
                    runBlocking { dataStoreManager.getAccessToken() } == null -> Screens.AuthScreen.route
                    else -> Screens.DogFeedScreen.route
                }
                val selectedTab = rememberSaveable { mutableStateOf(0) }
                val navController = rememberNavController()
                navController.addOnDestinationChangedListener { _, destination, arguments ->
                    val activeBottomTab = arguments?.getInt("activeBottomTab", -1) ?: -1
                    if (activeBottomTab != -1) selectedTab.value = activeBottomTab
                    when (destination.route) {
                        Screens.AuthScreen.route -> isBottomBarVisible.value = false
                        else -> isBottomBarVisible.value = true
                    }
                }
                Surface(color = MaterialTheme.colors.background) {
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
                LaunchedEffect(Unit) {
                    launch {
                        lifecycleAwareNavigationCommandsFlow.collect { command ->
                            command(navController)
                        }
                    }
                }
            }
        }
        //TODO what's the RL difference?
//        lifecycleScope.launchWhenResumed { observeNavigationCommands() }
    }

    private suspend fun observeNavigationCommands() {

    }

}


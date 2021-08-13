package com.skyyo.igdbbrowser.application.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.activity.cores.bottomBar.BottomBarCore
import com.skyyo.igdbbrowser.application.activity.cores.drawer.DrawerCore
import com.skyyo.igdbbrowser.application.persistance.DataStoreManager
import com.skyyo.igdbbrowser.theme.IgdbBrowserTheme
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
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
        //these boys won't be hoisted in the template
        val drawerOrBottomBarScreens = listOf(
            Screens.DogFeedScreen,
            Screens.Profile,
            Screens.UpcomingLaunches,
        )
        val startDestination = when {
            //TODO measure async + splash delegation profit
            runBlocking { dataStoreManager.getAccessToken() } == null -> Screens.AuthScreen.route
            else -> Screens.DogFeedScreen.route
        }
        setContent {
            IgdbBrowserTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    val lifecycleOwner = LocalLifecycleOwner.current
                    val navigationCommands = remember(navigationDispatcher.emitter, lifecycleOwner) {
                            navigationDispatcher.emitter.flowWithLifecycle(
                                lifecycleOwner.lifecycle,
                                Lifecycle.State.STARTED
                            )
                        }

                    BottomBarCore(
                        drawerOrBottomBarScreens,
                        startDestination,
                        navController
                    )
//                    DrawerCore(
//                        drawerOrBottomBarScreens,
//                        startDestination,
//                        navController
//                    )

                    //TODO difference between this & disposable effect in this scenario
                    LaunchedEffect(Unit) {
                        launch {
                            navigationCommands.collect { command -> command(navController) }
                        }
                    }
                }
            }
        }
    }

    private suspend fun observeNavigationCommands() {

    }

}


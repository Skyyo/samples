package com.skyyo.igdbbrowser.application.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.activity.cores.bottomBar.BottomBarCore
import com.skyyo.igdbbrowser.application.activity.cores.drawer.DrawerCore
import com.skyyo.igdbbrowser.application.persistance.DataStoreManager
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.AndroidEntryPoint
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
//            BottomBarCore(
//                navigationDispatcher,
//                drawerOrBottomBarScreens,
//                startDestination
//            )
            DrawerCore(
                navigationDispatcher,
                drawerOrBottomBarScreens,
                startDestination
            )
        }
        //TODO what's the RL difference?
        // lifecycleScope.launchWhenResumed { observeNavigationCommands() }
    }

    private suspend fun observeNavigationCommands() {

    }

}


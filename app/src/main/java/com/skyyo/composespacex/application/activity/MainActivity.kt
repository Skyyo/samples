package com.skyyo.composespacex.application.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.application.activity.cores.bottomBar.BottomBarCore
import com.skyyo.composespacex.application.activity.cores.drawer.DrawerCore
import com.skyyo.composespacex.application.persistance.DataStoreManager
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
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
            Screens.SamplesScreen,
        )
        val startDestination = when {
            //TODO measure async + splash delegation profit
            runBlocking { dataStoreManager.getAccessToken() } == null -> Screens.AuthScreen.route
            else -> Screens.DogFeedScreen.route
        }
        setContent {
            BottomBarCore(
                navigationDispatcher,
                drawerOrBottomBarScreens,
                startDestination
            )
//            DrawerCore(
//                navigationDispatcher,
//                drawerOrBottomBarScreens,
//                startDestination
//            )
        }
        //TODO what's the RL difference?
        // lifecycleScope.launchWhenResumed { observeNavigationCommands() }
    }

    private suspend fun observeNavigationCommands() {

    }

}


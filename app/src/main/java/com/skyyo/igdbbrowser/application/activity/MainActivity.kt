package com.skyyo.igdbbrowser.application.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.activity.cores.bottomBar.BottomBarCore
import com.skyyo.igdbbrowser.application.persistance.DataStoreManager
import com.skyyo.igdbbrowser.features.signIn.THEME_AUTO
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

    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    @ExperimentalMaterialNavigationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyEdgeToEdge()
        //these boys won't be hoisted in the template
        val drawerOrBottomBarScreens = listOf(
            Screens.DogFeedScreen,
            Screens.Profile,
            Screens.GamesScreen,
        )
        val startDestination = when {
            //TODO measure async + splash delegation profit
            runBlocking { dataStoreManager.getAccessToken() } == null -> Screens.AuthScreen.route
            else -> Screens.DogFeedScreen.route
        }
        //TODO can be optimized
//        val savedTheme = runBlocking { dataStoreManager.getAppTheme() }
        val savedTheme = THEME_AUTO

        setContent {
            IgdbBrowserTheme(savedTheme) {
                val systemUiController = rememberSystemUiController()
                ProvideWindowInsets {
                    Surface(color = MaterialTheme.colors.background) {
                        val bottomSheetNavigator = rememberBottomSheetNavigator()
                        val navController = rememberNavController()
                        navController.navigatorProvider += bottomSheetNavigator

                        val lifecycleOwner = LocalLifecycleOwner.current
                        val navigationCommands = remember(navigationDispatcher.emitter, lifecycleOwner) {
                                navigationDispatcher.emitter.flowWithLifecycle(
                                    lifecycleOwner.lifecycle,
                                    Lifecycle.State.STARTED
                                )
                            }

                        LaunchedEffect(Unit) {
                            launch {
                                navigationCommands.collect { command -> command(navController) }
                            }
                        }
                        // used only for the bottom sheet destinations
                        ModalBottomSheetLayout(bottomSheetNavigator) {
//                        SimpleCore(
//                            startDestination,
//                            navController
//                        )
                            BottomBarCore(
                                drawerOrBottomBarScreens,
                                startDestination,
                                navController,
                                systemUiController
                            )
//                    DrawerCore(
//                        drawerOrBottomBarScreens,
//                        startDestination,
//                        navController
//                    )
                        }
                    }
                }
            }
        }
    }

    private fun applyEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

//    fun changeSystemBars(light: Boolean) =
//        ViewCompat.getWindowInsetsController(this.v)?.let { controller ->
//            if (controller.isAppearanceLightStatusBars != light) {
//                controller.isAppearanceLightNavigationBars = light
//                controller.isAppearanceLightStatusBars = light
//            }
//        }
}


package com.skyyo.samples.application.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.plusAssign
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.persistance.DataStoreManager
import com.skyyo.samples.theme.IgdbBrowserTheme
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyEdgeToEdge()

        //TODO can be optimized. Shouldn't be used if we don't allow for manual theme switching,
        // unless we force light theme
        val savedTheme = runBlocking { dataStoreManager.getAppTheme() }

        setContent {
            val lifecycleOwner = LocalLifecycleOwner.current
            val navController = rememberAnimatedNavController()
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            navController.navigatorProvider += bottomSheetNavigator

            val navigationEvents = remember(navigationDispatcher.emitter, lifecycleOwner) {
                navigationDispatcher.emitter.flowWithLifecycle(
                    lifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
            }

            DisposableEffect(navController) {
                val callback = NavController.OnDestinationChangedListener { _, destination, args ->
                    when (destination.route) {
                        Destination.SampleContainer.route -> {
                        }
                        else -> {
                        }
                    }
                }
                navController.addOnDestinationChangedListener(callback)
                onDispose {
                    navController.removeOnDestinationChangedListener(callback)
                }
            }
            LaunchedEffect(Unit) {
                navigationEvents.collect { event -> event(navController) }
            }

            IgdbBrowserTheme(savedTheme) {
                ProvideWindowInsets {
                    // used only for the bottom sheet destinations
                    ModalBottomSheetLayout(bottomSheetNavigator) {
                        Scaffold {
                            PopulatedNavHost(
                                startDestination = Destination.SampleContainer.route,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }


    private fun applyEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}


package com.skyyo.samples.application.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.skyyo.samples.application.INTENT_ACTION_OPEN_FEATURE
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.INTENT_EXTRA_FEATURE_ROUTE
import com.skyyo.samples.application.persistance.DataStoreManager
import com.skyyo.samples.extensions.log
import com.skyyo.samples.features.userInteractionTrackingResult.IdlingSessionEvent
import com.skyyo.samples.features.userInteractionTrackingResult.IdlingSessionManager
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

    @Inject
    lateinit var idlingSessionManager: IdlingSessionManager

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyEdgeToEdge()
        // TODO can be optimized. Shouldn't be used if we don't allow for manual theme switching,
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
                val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
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
                // used only for the bottom sheet destinations
                ModalBottomSheetLayout(bottomSheetNavigator) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        PopulatedNavHost(
                            startDestination = provideStartDestination(intent),
                            navController = navController
                        )
                    }
                }
            }

            lifecycleScope.launchWhenResumed { observeIdlingSessionEvents() }
        }
        idlingSessionManager.startSession()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        idlingSessionManager.startSession()
    }

    private fun provideStartDestination(intent: Intent): String = when (intent.action) {
        INTENT_ACTION_OPEN_FEATURE -> {
            intent.extras?.getString(INTENT_EXTRA_FEATURE_ROUTE) ?: Destination.SampleContainer.route
        }
        else -> Destination.SampleContainer.route
    }

    private fun applyEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private suspend fun observeIdlingSessionEvents() {
        for (event in idlingSessionManager.eventDispatcher.eventEmitter) {
            when (event) {
                IdlingSessionEvent.StopSession -> goSessionExpiredScreen()
                IdlingSessionEvent.StartSession -> {
                    // we are starting it from Activity, so nothing to add here
                }
            }
        }
    }

    private fun goSessionExpiredScreen() {
        navigationDispatcher.emit {
            if (it.currentDestination?.route != Destination.SessionTimeExpired.route) {
                log("goSessionExpiredScreen")
                it.navigate(Destination.SessionTimeExpired.route)
            }
        }
    }
}

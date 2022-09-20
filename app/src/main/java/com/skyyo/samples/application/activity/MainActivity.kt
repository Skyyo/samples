package com.skyyo.samples.application.activity

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
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import com.skyyo.samples.utils.AppLifecycleObserver
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.AndroidEntryPoint
import com.skyyo.samples.utils.eventDispatchers.UserIdlingSession
import com.skyyo.samples.utils.eventDispatchers.UserIdlingSessionEventDispatcher
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

const val MILLIS_IN_SECOND = 1000L
const val SESSION_MAIN_TIME_SECONDS = 10
const val SESSION_EXTRA_TIME_SECONDS = 5

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher

    @Inject
    lateinit var userIdlingSessionEventDispatcher: UserIdlingSessionEventDispatcher

    /**
     * [userIdlingSessionTimer] variable represents a Timer which we start when the user signs in to the app.
     * When the method [onUserInteraction] is called, the [userIdlingSessionTimer] will be restarted.
     * If user is inactive for [SESSION_MAIN_TIME_SECONDS], the timer runs out and user
     * observes the result screen with additional time - [SESSION_EXTRA_TIME_SECONDS].
     */
    private var userIdlingSessionTimer: Timer? = null

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
            ProcessLifecycleOwner.get().lifecycle.addObserver(
                AppLifecycleObserver(
                    onMaximumIdlingTimeInBackgroundReached = ::onMaximumIdlingTimeInBackgroundReached
                )
            )
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
                // accompanist window insets not working well with in-app language change library
                // shouldn't be an issue, as we already adopted native insets, and this works fine
                ProvideWindowInsets {
                    // used only for the bottom sheet destinations
                    ModalBottomSheetLayout(bottomSheetNavigator) {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            PopulatedNavHost(
                                startDestination = Destination.SampleContainer.route,
                                navController = navController
                            )
                        }
                    }
                }
            }
            lifecycleScope.launchWhenResumed { observeUserIdlingSessionEvents() }
        }
    }

    private fun applyEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private suspend fun observeUserIdlingSessionEvents() {
        for (command in userIdlingSessionEventDispatcher.sessionEventEmitter) {
            when (command) {
                UserIdlingSession.StopSession -> {
                    if (isFinishing) return
                    finish()
                    startActivity(intent)
                }
                UserIdlingSession.StartSession -> {
                    startUserIdlingSession()
                }
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (userIdlingSessionTimer != null) startUserIdlingSession()
    }

    private fun startUserIdlingSession() {
        userIdlingSessionTimer?.cancel()
        userIdlingSessionTimer?.purge()
        userIdlingSessionTimer = Timer()
        userIdlingSessionTimer?.schedule(
            object : TimerTask() {
                override fun run() {
                    onMaximumIdlingTimeReached()
                }
            },
            SESSION_MAIN_TIME_SECONDS * MILLIS_IN_SECOND
        )
    }

    private fun onMaximumIdlingTimeReached() {
        userIdlingSessionTimer = null
        goUserInteractionTrackingResultScreen()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onMaximumIdlingTimeInBackgroundReached() {
        navigationDispatcher.emit {
            it.currentDestination?.route?.let { destination ->
                @Suppress("GlobalCoroutineUsage")
                GlobalScope.launch(Dispatchers.IO) {
                    when (destination) {
                        Destination.SampleContainer.route -> return@launch
                        else -> userIdlingSessionEventDispatcher.stopSession()
                    }
                }
            }
        }
    }

    private fun goUserInteractionTrackingResultScreen() {
        navigationDispatcher.emit { it.navigate(Destination.UserInteractionTrackingResult.route) }
    }
}

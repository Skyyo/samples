package com.skyyo.samples.application.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import androidx.navigation.ui.setupWithNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.skyyo.samples.R
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.persistance.DataStoreManager
import com.skyyo.samples.databinding.ActivityWithFragmentsBinding
import com.skyyo.samples.theme.IgdbBrowserTheme
import com.skyyo.samples.utils.NavigationDispatcher
import com.skyyo.samples.utils.supportedLanguages
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val USE_BOTTOM_NAVIGATION_WITH_FRAGMENTS = true
const val BOTTOM_NAVIGATION_HEIGHT = 56

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LanguageEntryPoint {
        fun getDataStoreManager(): DataStoreManager
    }

    private lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        configureLanguage()
        super.onCreate(savedInstanceState)
        applyEdgeToEdge()
        setContent()
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    private fun setContent() {
        if (USE_BOTTOM_NAVIGATION_WITH_FRAGMENTS) {
            val binding = ActivityWithFragmentsBinding.inflate(layoutInflater)
            setContentView(binding.root)
            (supportFragmentManager.findFragmentById(R.id.fragmentHost) as NavHostFragment).also { navHost ->
                val navInflater = navHost.navController.navInflater
                val navGraph = navInflater.inflate(R.navigation.nav_graph)
                navHost.navController.graph = navGraph
                val controller = navHost.navController
                binding.bnv.setupWithNavController(controller)

                lifecycleScope.launchWhenResumed {
                    navigationDispatcher.emitter.collect { it.invoke(controller) }
                }
            }
        } else {
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
    }

    private fun configureLanguage() {
        val accessors = EntryPointAccessors.fromApplication(this, LanguageEntryPoint::class.java)
        dataStoreManager = accessors.getDataStoreManager()
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentSystemLocale = currentLocales.getFirstMatch(supportedLanguages.map { it.code }.toTypedArray())
        if (currentSystemLocale != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                dataStoreManager.setLanguageCode(currentSystemLocale.language)
            }
        } else {
            val languageCode = runBlocking { dataStoreManager.getLanguageCode().first() }
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }

    private fun applyEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}


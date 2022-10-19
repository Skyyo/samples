package com.skyyo.samples.features.navigationCores.bottomBar

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.navigateToRootDestination
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.features.navigateWithResult.simple.dogContacts.DogContactsScreen
import com.skyyo.samples.features.navigateWithResult.simple.dogDetails.*
import com.skyyo.samples.features.navigateWithResult.simple.dogFeed.DogFeedScreen
import com.skyyo.samples.features.navigateWithResult.withObject.catContacts.CatContactsScreen
import com.skyyo.samples.features.navigateWithResult.withObject.catDetails.CatDetailsScreen
import com.skyyo.samples.features.navigateWithResult.withObject.catFeed.CatFeedScreen
import com.skyyo.samples.features.navigationCores.tab1.Tab1Screen
import com.skyyo.samples.features.navigationCores.tab2.Tab2Screen
import com.skyyo.samples.features.navigationCores.tab3.Tab3Screen
import com.skyyo.samples.features.pdfViewer.PdfViewerScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomBarCore(
    bottomBarScreens: List<Destination>,
    startDestination: Destination,
    navController: NavHostController,
    systemUiController: SystemUiController,
    viewModel: BottomBarViewModel = hiltViewModel()
) {
    val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
    val selectedTab = rememberSaveable { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                Destination.Tab3.route -> {
                    systemUiController.statusBarDarkContentEnabled = false
                    isBottomBarVisible.value = true
                }
                else -> {
                    isBottomBarVisible.value = true
                }
            }
        }
        navController.addOnDestinationChangedListener(callback)
        onDispose {
            navController.removeOnDestinationChangedListener(callback)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val navigationDispatcher = viewModel.navigationDispatcher
    val navigationEvents = remember(navigationDispatcher.bottomBarNavControllerEvents, lifecycleOwner) {
        navigationDispatcher.bottomBarNavControllerEvents.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }
    // we need to wait for verification server to wake up until deep links will work
    // deep links can be tested with
    // adb shell am start -W -a android.intent.action.VIEW -d https://abrupt-tabby-medusaceratops.glitch.me/confirmEmail?dogId=10
    var isVerificationServerReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch { navigationEvents.collect { event -> event(navController) } }
        launch {
            viewModel.waitForVerificationServerToWakeUp()
            isVerificationServerReady = true
        }
    }
    val dogDetailsTransitionProvider = remember { DogDetailsTransitionsProvider() }

    Box {
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination.route,
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 350)) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 350)) },
            modifier = Modifier.padding(bottom = if (isBottomBarVisible.value) BOTTOM_BAR_HEIGHT else 0.dp)
        ) {
            composable(Destination.Tab1.route) { Tab1Screen() }
            composable(Destination.Tab2.route) { Tab2Screen(withBottomBar = true) }
            composable(Destination.Tab3.route) { Tab3Screen(withBottomBar = true) }
            dogFeedComposable(dogDetailsTransitionProvider)
            dogDetailsComposable(dogDetailsTransitionProvider)
            composable(Destination.DogContacts.route) { DogContactsScreen() }
            composable(Destination.CatFeed.route) { CatFeedScreen() }
            composable(Destination.CatDetails.route) { CatDetailsScreen() }
            composable(Destination.CatContacts.route) { CatContactsScreen() }
            deepLinkComposable(
                route = Destination.PdfViewer,
                startDestination = startDestination,
                backStackDestinations = listOf(Destination.Tab2, Destination.DogFeed, Destination.DogDetails),
                authorisedBackStackDestinations = listOf(Destination.Tab3, Destination.CatFeed),
                isAuthorised = {
                    true
                },
                deepLinkUri = "https://abrupt-tabby-medusaceratops.glitch.me/confirmEmail",
                deepLinkParams = setOf("dogId"),
                navController = navController
            ) {
                PdfViewerScreen()
            }
        }
        AnimatedBottomBar(
            Modifier.align(Alignment.BottomCenter),
            bottomBarScreens,
            selectedTab.value,
            isBottomBarVisible.value
        ) { index, route ->
            // this means we're already on the selected tab
            if (index == selectedTab.value) return@AnimatedBottomBar
            val selectedRoute = bottomBarScreens[selectedTab.value]
            selectedTab.value = index
            navController.navigateToRootDestination(
                route = route,
                popInclusive = true,
                popUpToRoute = selectedRoute.route
            )
        }

        if (!isVerificationServerReady) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.dogFeedComposable(
    dogDetailsTransitionsProvider: DogDetailsTransitionsProvider
) {
    composable(
        route = Destination.DogFeed.route,
        popEnterTransition = {
            dogDetailsTransitionsProvider.getPreviousDestinationPopEnterTransition()
        }
    ) {
        DogFeedScreen()
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.dogDetailsComposable(
    dogDetailsTransitionsProvider: DogDetailsTransitionsProvider
) {
    composable(
        route = Destination.DogDetails.route,
        enterTransition = {
            val startAnimationRect = initialState.savedStateHandle.get<String>(START_ANIMATION_RECT_KEY)
            val endAnimationRect = initialState.savedStateHandle.get<String>(END_ANIMATION_RECT_KEY)
            when {
                startAnimationRect == null || endAnimationRect == null -> {
                    fadeIn(animationSpec = tween(durationMillis = 350))
                }
                else -> {
                    dogDetailsTransitionsProvider.getEnterTransition(
                        startAnimationRect = Rect.unflattenFromString(startAnimationRect)!!.toComposeRect(),
                        endAnimationRect = Rect.unflattenFromString(endAnimationRect)!!.toComposeRect(),
                    )
                }
            }
        },
        exitTransition = { fadeOut(animationSpec = tween(durationMillis = 350)) },
        popExitTransition = {
            val startAnimationRect = targetState.savedStateHandle.get<String>(START_ANIMATION_RECT_KEY)
            val endAnimationRect = targetState.savedStateHandle.get<String>(END_ANIMATION_RECT_KEY)
            when {
                startAnimationRect == null || endAnimationRect == null -> {
                    fadeOut(animationSpec = tween(durationMillis = 350))
                }
                else -> {
                    dogDetailsTransitionsProvider.getExitTransition(
                        startAnimationRect = Rect.unflattenFromString(endAnimationRect)!!.toComposeRect(),
                        endAnimationRect = Rect.unflattenFromString(startAnimationRect)!!.toComposeRect(),
                    )
                }
            }
        },
        popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 350)) }
    ) {
        DogDetailsScreen()
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.deepLinkComposable(
    route: Destination,
    startDestination: Destination,
    backStackDestinations: List<Destination>,
    authorisedBackStackDestinations: List<Destination>,
    isAuthorised: suspend () -> Boolean,
    deepLinkUri: String,
    deepLinkParams: Set<String>,
    navController: NavController,
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    val deepLinkQuery = deepLinkParams.joinToString(separator = "&") { "$it={$it}" }
    composable(
        route = route.route,
        deepLinks = listOf(navDeepLink { uriPattern = "$deepLinkUri?$deepLinkQuery" }),
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition
    ) {
        val isAuthorisedState = remember { Ref<Boolean>() }
        LaunchedEffect(Unit) {
            isAuthorisedState.value = isAuthorised()
        }

        BackHandler {
            val args = it.arguments?.apply {
                keySet().forEach { key -> if (!deepLinkParams.contains(key)) remove(key) }
            } ?: Bundle()
            // we want to use bottom bar core nav controller instead of activity nav controller
            args.putBoolean(WITH_BOTTOM_BAR_KEY, true)
            val destinationsToRestore = when (isAuthorisedState.value) {
                true -> authorisedBackStackDestinations
                else -> backStackDestinations
            }.toMutableList()

            val bottomBarNavigationRoute = destinationsToRestore.removeFirst()
            navController.navigateToRootDestination(
                route = bottomBarNavigationRoute.route,
                popInclusive = true,
                popUpToRoute = startDestination.route
            )
            destinationsToRestore.forEach { destination ->
                navController.navigateWithObject(route = destination.route, arguments = args)
            }
        }

        content(it)
    }
}

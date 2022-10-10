package com.skyyo.samples.features.navigationCores.bottomBar

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.skyyo.samples.features.navigateWithResult.simple.dogContacts.DogContactsScreen
import com.skyyo.samples.features.navigateWithResult.simple.dogDetails.DogDetailsScreen
import com.skyyo.samples.features.navigateWithResult.simple.dogFeed.DogFeedScreen
import com.skyyo.samples.features.navigateWithResult.withObject.catContacts.CatContactsScreen
import com.skyyo.samples.features.navigateWithResult.withObject.catDetails.CatDetailsScreen
import com.skyyo.samples.features.navigateWithResult.withObject.catFeed.CatFeedScreen
import com.skyyo.samples.features.navigationCores.tab1.Tab1Screen
import com.skyyo.samples.features.navigationCores.tab2.Tab2Screen
import com.skyyo.samples.features.navigationCores.tab3.Tab3Screen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomBarCore(
    bottomBarScreens: List<Destination>,
    startDestination: String,
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

    LaunchedEffect(Unit) {
        navigationEvents.collect { event -> event(navController) }
    }

    Box {
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 350)) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 350)) },
            modifier = Modifier.padding(bottom = if (isBottomBarVisible.value) BOTTOM_BAR_HEIGHT else 0.dp)
        ) {
            composable(Destination.Tab1.route) { Tab1Screen() }
            composable(Destination.Tab2.route) { Tab2Screen(withBottomBar = true) }
            composable(Destination.Tab3.route) { Tab3Screen(withBottomBar = true) }
            composable(Destination.DogFeed.route) { DogFeedScreen() }
            composable(Destination.DogDetails.route) { DogDetailsScreen() }
            composable(Destination.DogContacts.route) { DogContactsScreen() }
            composable(Destination.CatFeed.route) { CatFeedScreen() }
            composable(Destination.CatDetails.route) { CatDetailsScreen() }
            composable(Destination.CatContacts.route) { CatContactsScreen() }
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
    }
}

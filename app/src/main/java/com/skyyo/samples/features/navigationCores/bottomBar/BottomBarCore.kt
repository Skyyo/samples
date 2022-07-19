package com.skyyo.samples.features.navigationCores.bottomBar

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.navigateToRootDestination
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
) {
    val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
    val selectedTab = rememberSaveable { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val callback = NavController.OnDestinationChangedListener { _, destination, args ->
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

    Box {
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 350)) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 350)) },
            modifier = Modifier.padding(0.dp)
        ) {

            composable(Destination.Tab1.route) { Tab1Screen() }
            composable(Destination.Tab2.route) {
                BackHandler(onBack = {
                    selectedTab.value = 0
                    navController.navigateToRootDestination(Destination.Tab1.route)
                })
                Tab2Screen()
            }
            composable(Destination.Tab3.route) {
                BackHandler(onBack = {
                    selectedTab.value = 0
                    navController.navigateToRootDestination(Destination.Tab1.route)
                })
                Tab3Screen()
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
            selectedTab.value = index
            navController.navigateToRootDestination(route)
        }
    }
}

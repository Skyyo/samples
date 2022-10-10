package com.skyyo.samples.features.navigationCores.bottomBar

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.skyyo.samples.application.Destination

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomBarScreen() {
    val screens = remember {
        listOf(
            Destination.Tab1,
            Destination.Tab2,
            Destination.Tab3,
        )
    }
    val navController = rememberAnimatedNavController()
    val systemUiController = rememberSystemUiController()

    BottomBarCore(
        bottomBarScreens = screens,
        startDestination = screens.first(),
        navController = navController,
        systemUiController = systemUiController
    )
}

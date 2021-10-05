package com.skyyo.samples.features.navigationCores.drawer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.skyyo.samples.application.Destination

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun DrawerScreen() {
    val screens = remember {
        listOf(
            Destination.Tab1,
            Destination.Tab2,
            Destination.Tab3,
        )
    }
    val navController = rememberAnimatedNavController()
    val systemUiController = rememberSystemUiController()

    DrawerCore(
        drawerScreens = screens,
        startDestination = Destination.Tab1.route,
        navController = navController,
        systemUiController = systemUiController
    )
}
package com.skyyo.samples.application.activity.cores.simple

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.skyyo.samples.application.Screens
import com.skyyo.samples.application.activity.PopulatedNavHost
import com.skyyo.samples.extensions.log

@Composable
fun SimpleCore(
    startDestination: String,
    navController: NavHostController
) {
    DisposableEffect(navController) {
        val callback = NavController.OnDestinationChangedListener { _, destination, args ->
            log("${destination.route}")
            when (destination.route) {
                Screens.SampleContainer.route -> {
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

    Scaffold(
        content = { innerPadding ->
            PopulatedNavHost(
                startDestination = startDestination,
                innerPadding = innerPadding,
                navController = navController
            )
        })
}
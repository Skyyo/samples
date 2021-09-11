package com.skyyo.igdbbrowser.application.activity.cores.bottomBar

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.activity.PopulatedNavHost
import com.skyyo.igdbbrowser.extensions.navigateToRootDestination

@Composable
fun BottomBarCore(
    bottomBarScreens: List<Screens>,
    startDestination: String,
    navController: NavHostController,
    systemUiController: SystemUiController,
) {
    val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
    val selectedTab = rememberSaveable { mutableStateOf(0) }
//    val backgroundColour = remember { mutableStateOf(Color.Black) }
    DisposableEffect(Unit) {
        val callback = NavController.OnDestinationChangedListener { _, destination, args ->
//            log("${destination.route}")
//            log("first destination:${navController.findDestination(navController.graph.findStartDestination().id)}")
            //TODO make args based?
            systemUiController.statusBarDarkContentEnabled = false
            when (destination.route) {
                Screens.SampleContainer.route -> isBottomBarVisible.value = false
//                Screens.Profile.route -> backgroundColour.value = Color.Green
                else -> {
                    isBottomBarVisible.value = true
//                    backgroundColour.value = Color.Black
                }
            }
        }
        navController.addOnDestinationChangedListener(callback)
        onDispose {
            navController.removeOnDestinationChangedListener(callback)
        }
    }

    Scaffold(
//        backgroundColor = backgroundColour.value,
        bottomBar = {
            AnimatedBottomBar(
                bottomBarScreens,
                selectedTab.value,
                isBottomBarVisible.value
            ) { index, route ->
                //this means we're already on the selected tab
                if (index == selectedTab.value) return@AnimatedBottomBar
                selectedTab.value = index
                navController.navigateToRootDestination(route)
            }
        },
        content = { innerPadding ->
            PopulatedNavHost(
                startDestination = startDestination,
                // we can remove this padding to allow content draw like in a Box()
                innerPadding = innerPadding,
                navController = navController,
                onBackPressIntercepted = {
                    selectedTab.value = 0
                    navController.navigateToRootDestination(Screens.DogFeed.route)
                }
            )
        })
}
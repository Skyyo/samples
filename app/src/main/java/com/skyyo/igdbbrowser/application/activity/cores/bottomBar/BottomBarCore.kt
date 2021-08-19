package com.skyyo.igdbbrowser.application.activity.cores.bottomBar

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.activity.PopulatedNavHost
import com.skyyo.igdbbrowser.extensions.navigateToRootDestination

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalMaterialNavigationApi
@Composable
@ExperimentalAnimationApi
fun BottomBarCore(
    bottomBarScreens: List<Screens>,
    startDestination: String,
    navController: NavHostController,
    systemUiController: SystemUiController
) {
    val isBottomBarVisible = rememberSaveable { mutableStateOf(false) }
    val selectedTab = rememberSaveable { mutableStateOf(0) }
    DisposableEffect(navController) {
        val callback = NavController.OnDestinationChangedListener { _, destination, args ->
//            log("${destination.route}")
//            log("first destination:${navController.findDestination(navController.graph.findStartDestination().id)}")
            //TODO make args based?
            systemUiController.statusBarDarkContentEnabled = false
            when (destination.route) {
                Screens.SignIn.route -> isBottomBarVisible.value = false
                else -> isBottomBarVisible.value = true
            }
        }
        navController.addOnDestinationChangedListener(callback)
        onDispose {
            navController.removeOnDestinationChangedListener(callback)
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedBottomBar(
                bottomBarScreens,
                selectedTab.value,
                isBottomBarVisible.value
            ) { index, route ->
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
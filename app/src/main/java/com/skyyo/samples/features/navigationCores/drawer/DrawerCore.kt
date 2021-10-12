package com.skyyo.samples.features.navigationCores.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@Composable
fun DrawerCore(
    drawerScreens: List<Destination>,
    startDestination: String,
    navController: NavHostController,
    systemUiController: SystemUiController
) {
    val isDrawerVisible = rememberSaveable { mutableStateOf(false) }
    val selectedTab = rememberSaveable { mutableStateOf(0) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val animationSpec = remember { tween<Float>(500) }

    DisposableEffect(Unit) {
        val callback = NavController.OnDestinationChangedListener { _, destination, args ->
            when (destination.route) {
//                Destination.Tab3.route -> {
//                    systemUiController.statusBarDarkContentEnabled = false
//                    isDrawerVisible.value = false
//                }
                else -> {
                    systemUiController.statusBarDarkContentEnabled = true
                    isDrawerVisible.value = true
                }
            }
        }
        navController.addOnDestinationChangedListener(callback)
        onDispose {
            navController.removeOnDestinationChangedListener(callback)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        //this allows to dismiss the drawer if its open by tapping on dimmed area
        drawerGesturesEnabled = scaffoldState.drawerState.let { it.isOpen && !it.isAnimationRunning },
        floatingActionButton = {
            if (isDrawerVisible.value) {
                FloatingActionButton(onClick = {
                    scope.launch {
                        scaffoldState.drawerState.animateTo(
                            DrawerValue.Open,
                            animationSpec
                        )
                    }
                }) {
                    Text(text = "open drawer")
                }
            }
        },
        drawerContent = {
            if (isDrawerVisible.value) {
                Drawer(
                    screens = drawerScreens,
                    selectedTab = selectedTab.value
                ) { index, route ->
                    //this means we're already on the selected tab
                    if (index != selectedTab.value) {
                        selectedTab.value = index
                        navController.navigateToRootDestination(route)
                    }
                    //Skip closing drawer if it's not opened completely
                    if (scaffoldState.drawerState.let { it.isClosed && it.isAnimationRunning }) return@Drawer
                    scope.launch {
                        scaffoldState.drawerState.animateTo(
                            DrawerValue.Closed,
                            animationSpec
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            AnimatedNavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = { _, _ -> fadeIn(animationSpec = tween(350)) },
                exitTransition = { _, _ -> fadeOut(animationSpec = tween(350)) },
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
        }
    )
}



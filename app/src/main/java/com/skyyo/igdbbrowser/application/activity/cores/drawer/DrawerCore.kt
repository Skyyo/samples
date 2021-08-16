package com.skyyo.igdbbrowser.application.activity.cores.drawer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.pager.ExperimentalPagerApi
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.activity.PopulatedNavHost
import com.skyyo.igdbbrowser.extensions.log
import com.skyyo.igdbbrowser.extensions.navigateToRootDestination
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalMaterialNavigationApi
@Composable
@ExperimentalAnimationApi
fun DrawerCore(
    drawerScreens: List<Screens>,
    startDestination: String,
    navController: NavHostController
) {
    val isDrawerVisible = rememberSaveable { mutableStateOf(false) }
    val selectedTab = rememberSaveable { mutableStateOf(0) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    DisposableEffect(navController) {
        val callback = NavController.OnDestinationChangedListener { _, destination, args ->
            log("${destination.route}")
            when (destination.route) {
                Screens.AuthScreen.route -> isDrawerVisible.value = false
                else -> isDrawerVisible.value = true
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
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        floatingActionButton = {
            if (isDrawerVisible.value) {
                FloatingActionButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
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
                    selectedTab.value = index
                    navController.navigateToRootDestination(route)
                    scope.launch { scaffoldState.drawerState.close() }
                }
            }
        },
        content = { innerPadding ->
            PopulatedNavHost(
                startDestination = startDestination,
                innerPadding = innerPadding,
                navController = navController,
                onBackPressIntercepted = {
                    selectedTab.value = 0
                    navController.navigateToRootDestination(Screens.DogFeedScreen.route)
                }
            )
        }
    )
}



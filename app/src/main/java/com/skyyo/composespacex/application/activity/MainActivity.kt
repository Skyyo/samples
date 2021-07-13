package com.skyyo.composespacex.application.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.extensions.log
import com.skyyo.composespacex.theme.ComposeSpaceXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSpaceXTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val bottomNavScreens = listOf(
                        Screens.DogFeedScreen,
                        Screens.Profile,
                        Screens.FriendsList,
                    )
                    val navController = rememberNavController()
                    val selectedTab = rememberSaveable { mutableStateOf(0) }
                    Scaffold(
                        bottomBar = {
                            BottomBar(bottomNavScreens, selectedTab.value) { index, route ->
                                selectedTab.value = index
                                navController.navigateToBottomNavDestination(route)
                            }
                        },
                        content = { innerPadding ->
                            PopulatedNavHost(
                                startDestination = bottomNavScreens.first().route,
                                innerPadding = innerPadding,
                                navController = navController,
                                onBackPressFromTabIntercepted = {
                                    selectedTab.value = 0
                                    navController.navigateToBottomNavDestination(bottomNavScreens.first().route)
                                }
                            )
                        })
                }
            }
        }
    }
    private fun NavController.navigateToBottomNavDestination(route: String) {
        navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

}


package com.skyyo.igdbbrowser.application.activity

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.skyyo.igdbbrowser.application.DogDetailsGraph
import com.skyyo.igdbbrowser.application.EditProfileGraph
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.features.dog.DogContactsScreen
import com.skyyo.igdbbrowser.features.dog.DogDetailsScreen
import com.skyyo.igdbbrowser.features.dog.DogFeedScreen
import com.skyyo.igdbbrowser.features.launches.LaunchesList
import com.skyyo.igdbbrowser.features.profile.*
import com.skyyo.igdbbrowser.features.signIn.SignInScreen
import com.skyyo.igdbbrowser.features.signIn.forceTheme.ForceThemeScreen
import com.skyyo.igdbbrowser.features.signIn.googleMap.MapScreen

@Composable
fun PopulatedNavHost(
    startDestination: String,
    innerPadding: PaddingValues,
    navController: NavHostController,
    onBackPressIntercepted: (() -> Unit)? = null
) = NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = Modifier.padding(innerPadding)
) {
    composable(Screens.AuthScreen.route) { SignInScreen() }

    composable(Screens.DogFeedScreen.route) { DogFeedScreen() }
    navigation(
        route = DogDetailsGraph.route,
        startDestination = DogDetailsGraph.DogDetails.route
    ) {
        composable(route = DogDetailsGraph.DogDetails.route) { DogDetailsScreen() }
        composable(route = DogDetailsGraph.DogContacts.route) { DogContactsScreen() }
    }

    composable(Screens.Profile.route) {
        onBackPressIntercepted?.let { BackHandler(onBack = it) }
        Profile()
    }
    composable(Screens.UpcomingLaunches.route) {
        onBackPressIntercepted?.let { BackHandler(onBack = it) }
        LaunchesList()
    }
    navigation(
        route = EditProfileGraph.route,
        startDestination = EditProfileGraph.EditProfile.route
    ) {
        composable(route = EditProfileGraph.EditProfile.route) { EditProfile() }
        composable(route = EditProfileGraph.EditProfileConfirmation.route) {
            val navGraphViewModel: SharedProfileViewModel =
                hiltViewModel(navController.getBackStackEntry(EditProfileGraph.EditProfile.route))
            EditProfileConfirmation(navGraphViewModel)
        }
        composable(route = EditProfileGraph.EditProfileConfirmation2.route) {
            val navGraphViewModel: SharedProfileViewModel =
                hiltViewModel(navController.getBackStackEntry(EditProfileGraph.EditProfile.route))
            EditProfileConfirmation2(navGraphViewModel)
        }
    }

    composable(Screens.MapScreen.route) { MapScreen() }
    composable(Screens.ForceThemeScreen.route) { ForceThemeScreen() }

//    composable(Screens.FriendDetails.route) { FriendsDetails() }

}
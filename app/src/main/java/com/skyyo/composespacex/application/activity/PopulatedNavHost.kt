package com.skyyo.composespacex.application.activity

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
import com.skyyo.composespacex.application.DogDetailsGraph
import com.skyyo.composespacex.application.EditProfileGraph
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.features.signIn.AuthScreen
import com.skyyo.composespacex.features.dog.DogContactsScreen
import com.skyyo.composespacex.features.dog.DogDetailsScreen
import com.skyyo.composespacex.features.dog.DogFeedScreen
import com.skyyo.composespacex.features.launches.LaunchesList
import com.skyyo.composespacex.features.profile.*

@Composable
fun PopulatedNavHost(
    startDestination: String,
    innerPadding: PaddingValues,
    navController: NavHostController,
    onBackPressIntercepted: () -> Unit
) = NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = Modifier.padding(innerPadding)
) {
    composable(Screens.AuthScreen.route) { AuthScreen() }

    composable(Screens.DogFeedScreen.route) { DogFeedScreen() }
    navigation(
        route = DogDetailsGraph.route,
        startDestination = DogDetailsGraph.DogDetails.route
    ) {
        composable(route = DogDetailsGraph.DogDetails.route) { DogDetailsScreen() }
        composable(route = DogDetailsGraph.DogContacts.route) { DogContactsScreen() }
    }

    composable(Screens.Profile.route) {
        BackHandler(onBack = onBackPressIntercepted)
        Profile()
    }
    composable(Screens.UpcomingLaunches.route) {
        BackHandler(onBack = onBackPressIntercepted)
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
//    composable(Screens.FriendDetails.route) { FriendsDetails() }

}
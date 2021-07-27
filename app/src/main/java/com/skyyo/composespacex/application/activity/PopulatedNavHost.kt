package com.skyyo.composespacex.application.activity

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.skyyo.composespacex.application.DogDetailsGraph
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.features.auth.signIn.AuthScreen
import com.skyyo.composespacex.features.dog.DogContactsScreen
import com.skyyo.composespacex.features.dog.DogDetailsScreen
import com.skyyo.composespacex.features.dog.DogFeedScreen
import com.skyyo.composespacex.features.friends.FriendContactsScreen
import com.skyyo.composespacex.features.friends.FriendsDetails
import com.skyyo.composespacex.features.friends.FriendsList
import com.skyyo.composespacex.features.profile.Profile

@Composable
fun PopulatedNavHost(
    startDestination: String,
    innerPadding: PaddingValues,
    navController: NavHostController,
    onBackPressIntercepted: () -> Unit
) {
    return NavHost(
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

        composable(Screens.FriendsList.route) {
            BackHandler(onBack = onBackPressIntercepted)
            FriendsList()
        }
        composable(Screens.FriendDetails.route) { FriendsDetails() }
        composable(Screens.FriendContacts.route) { FriendContactsScreen() }

    }
}
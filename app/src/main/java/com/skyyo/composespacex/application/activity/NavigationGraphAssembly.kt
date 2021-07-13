package com.skyyo.composespacex.application.activity

import androidx.activity.compose.BackHandler
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.skyyo.composespacex.application.DogDetailsGraph
import com.skyyo.composespacex.features.dog.DogContactsScreen
import com.skyyo.composespacex.features.dog.DogDetailsScreen
import com.skyyo.composespacex.features.dog.DogFeedScreen
import com.skyyo.composespacex.features.friends.FriendContactsScreen
import com.skyyo.composespacex.features.friends.FriendsDetails
import com.skyyo.composespacex.features.friends.FriendsList
import com.skyyo.composespacex.features.profile.Profile
import com.skyyo.composespacex.application.Screens

fun NavGraphBuilder.addProfileTab(
    navController: NavHostController,
    onBackPressIntercepted: () -> Unit
) = composable(Screens.Profile.route) {
    BackHandler(onBack = { onBackPressIntercepted() })
    Profile()
}

fun NavGraphBuilder.addFriendsListTab(
    navController: NavHostController,
    onBackPressIntercepted: () -> Unit
) = composable(Screens.FriendsList.route) {
    BackHandler(onBack = { onBackPressIntercepted() })
    FriendsList(openFriendDetailsScreen = { navController.navigate(Screens.FriendDetails.route) })
}

fun NavGraphBuilder.addFriendDetails(navController: NavController) =
    composable(Screens.FriendDetails.route) {
        FriendsDetails { navController.navigate(Screens.FriendContacts.route) }
    }

fun NavGraphBuilder.addFriendContacts(navController: NavController) =
    composable(Screens.FriendContacts.route) {
        FriendContactsScreen {
            navController.popBackStack(Screens.FriendDetails.route, true)
        }
    }

fun NavGraphBuilder.addDogFeedTab(navController: NavController) =
    composable(Screens.DogFeedScreen.route) {
        DogFeedScreen {
            navController.navigate(DogDetailsGraph.DogDetails.createRoute(dogId = "2222"))
        }
    }

fun NavGraphBuilder.addDogDetailsGraph(navController: NavController) = navigation(
    route = DogDetailsGraph.route,
    startDestination = DogDetailsGraph.DogDetails.route
) {
    composable(route = DogDetailsGraph.DogDetails.route) { backStackEntry ->
        val dogId = backStackEntry.arguments?.getString("dogId")
        requireNotNull(dogId) { "dog id can't be null" }
        DogDetailsScreen(dogId = dogId) {
            navController.navigate(DogDetailsGraph.DogContacts.createRoute("3333"))
        }
    }
    composable(route = DogDetailsGraph.DogContacts.route) { backStackEntry ->
        val dogId = backStackEntry.arguments?.getString("dogId")
        requireNotNull(dogId) { "dog id can't be null" }
        DogContactsScreen(dogId) {
            navController.popBackStack(DogDetailsGraph.DogDetails.route, true)
        }
    }
}


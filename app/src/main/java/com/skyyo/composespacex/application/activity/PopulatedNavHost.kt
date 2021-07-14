package com.skyyo.composespacex.application.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.skyyo.composespacex.application.DogDetailsGraph
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.extensions.getNavigationResult
import com.skyyo.composespacex.features.dog.DogFeedScreen

@Composable
fun PopulatedNavHost(
    startDestination: String,
    innerPadding: PaddingValues,
    navController: NavHostController,
    onBackPressFromTabIntercepted: () -> Unit

) {
    return NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        addAuthScreen(navController)
        addDogFeedTab(navController)
        addProfileTab(navController) { onBackPressFromTabIntercepted() }
        addFriendsListTab(navController) { onBackPressFromTabIntercepted() }
        addFriendDetails(navController)
        addFriendContacts(navController)
        addDogDetailsGraph(navController)
    }
}
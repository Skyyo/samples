package com.skyyo.composespacex.application.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

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
        addDogFeedTab(navController)
        addProfileTab(navController) { onBackPressFromTabIntercepted() }
        addFriendsListTab(navController) { onBackPressFromTabIntercepted() }
        addFriendDetails(navController)
        addFriendContacts(navController)
        addDogDetailsGraph(navController)
    }
}
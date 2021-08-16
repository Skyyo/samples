package com.skyyo.igdbbrowser.application.activity

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.skyyo.igdbbrowser.application.DogDetailsGraph
import com.skyyo.igdbbrowser.application.EditProfileGraph
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.features.samples.navigateWithResult.DogContactsScreen
import com.skyyo.igdbbrowser.features.samples.navigateWithResult.DogDetailsScreen
import com.skyyo.igdbbrowser.features.samples.navigateWithResult.DogFeedScreen
import com.skyyo.igdbbrowser.features.launches.LaunchesList
import com.skyyo.igdbbrowser.features.profile.*
import com.skyyo.igdbbrowser.features.signIn.SignInScreen
import com.skyyo.igdbbrowser.features.samples.bottomSheets.BottomSheetScaffoldScreen
import com.skyyo.igdbbrowser.features.samples.bottomSheets.BottomSheetScreen
import com.skyyo.igdbbrowser.features.samples.bottomSheets.ModalBottomSheetScreen
import com.skyyo.igdbbrowser.features.samples.forceTheme.ForceThemeScreen
import com.skyyo.igdbbrowser.features.samples.googleMap.MapScreen
import com.skyyo.igdbbrowser.features.samples.viewPager.ViewPagerScreen

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalMaterialNavigationApi
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


    bottomSheet(Screens.BottomSheetScreen.route) { BottomSheetScreen() }
    composable(Screens.ModalBottomSheetScreen.route) { ModalBottomSheetScreen() }
    composable(Screens.BottomSheetScaffoldScreen.route) { BottomSheetScaffoldScreen() }
    composable(Screens.ViewPagerScreen.route) { ViewPagerScreen() }
//    composable(Screens.FriendDetails.route) { FriendsDetails() }

}
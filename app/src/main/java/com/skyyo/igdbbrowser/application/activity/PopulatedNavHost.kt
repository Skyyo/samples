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
import com.skyyo.igdbbrowser.features.games.GamesScreen
import com.skyyo.igdbbrowser.features.profile.*
import com.skyyo.igdbbrowser.features.samples.bottomSheets.BottomSheetScaffoldScreen
import com.skyyo.igdbbrowser.features.samples.bottomSheets.BottomSheetScreen
import com.skyyo.igdbbrowser.features.samples.bottomSheets.ModalBottomSheetScreen
import com.skyyo.igdbbrowser.features.samples.forceTheme.ForceThemeScreen
import com.skyyo.igdbbrowser.features.samples.googleMap.GoogleMapScreen
import com.skyyo.igdbbrowser.features.samples.inputValidations.auto.InputValidationAutoScreen
import com.skyyo.igdbbrowser.features.samples.inputValidations.autoDebounce.InputValidationAutoDebounceScreen
import com.skyyo.igdbbrowser.features.samples.inputValidations.manual.InputValidationManualScreen
import com.skyyo.igdbbrowser.features.samples.lists.ListsScreen
import com.skyyo.igdbbrowser.features.samples.navigateWithResult.DogContactsScreen
import com.skyyo.igdbbrowser.features.samples.navigateWithResult.DogDetailsScreen
import com.skyyo.igdbbrowser.features.samples.navigateWithResult.DogFeedScreen
import com.skyyo.igdbbrowser.features.samples.viewPager.ViewPagerScreen
import com.skyyo.igdbbrowser.features.signIn.SignInScreen

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
    composable(Screens.SignIn.route) { SignInScreen() }
    composable(Screens.Profile.route) {
        onBackPressIntercepted?.let { BackHandler(onBack = it) }
        ProfileScreen()
    }
    composable(Screens.Games.route) {
        onBackPressIntercepted?.let { BackHandler(onBack = it) }
        GamesScreen()
    }
    navigation(
        route = EditProfileGraph.route,
        startDestination = EditProfileGraph.EditProfile.route
    ) {
        composable(route = EditProfileGraph.EditProfile.route) { EditProfileScreen() }
        composable(route = EditProfileGraph.EditProfileConfirmation.route) {
            val navGraphSharedViewModel: ProfileSharedViewModel =
                hiltViewModel(navController.getBackStackEntry(EditProfileGraph.EditProfile.route))
            EditProfileConfirmationScreen(navGraphSharedViewModel)
        }
        composable(route = EditProfileGraph.EditProfileConfirmation2.route) {
            val navGraphSharedViewModel: ProfileSharedViewModel =
                hiltViewModel(navController.getBackStackEntry(EditProfileGraph.EditProfile.route))
            EditProfileConfirmation2Screen(navGraphSharedViewModel)
        }
    }


    composable(Screens.DogFeed.route) { DogFeedScreen() }
    navigation(
        route = DogDetailsGraph.route,
        startDestination = DogDetailsGraph.DogDetails.route
    ) {
        composable(route = DogDetailsGraph.DogDetails.route) { DogDetailsScreen() }
        composable(route = DogDetailsGraph.DogContacts.route) { DogContactsScreen() }
    }
    composable(Screens.Map.route) { GoogleMapScreen() }
    composable(Screens.ForceTheme.route) { ForceThemeScreen() }
    bottomSheet(Screens.BottomSheet.route) { BottomSheetScreen() }
    composable(Screens.ModalBottomSheet.route) { ModalBottomSheetScreen() }
    composable(Screens.BottomSheetScaffold.route) { BottomSheetScaffoldScreen() }
    composable(Screens.ViewPager.route) { ViewPagerScreen() }
    composable(Screens.Lists.route) { ListsScreen() }
    composable(Screens.InputValidationManual.route) { InputValidationManualScreen() }
    composable(Screens.InputValidationAuto.route) { InputValidationAutoScreen() }
    composable(Screens.InputValidationDebounce.route) { InputValidationAutoDebounceScreen() }

}
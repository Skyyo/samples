package com.skyyo.igdbbrowser.application.activity

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.skyyo.igdbbrowser.application.DogDetailsGraph
import com.skyyo.igdbbrowser.application.EditProfileGraph
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.features.autoscroll.AutoScrollScreen
import com.skyyo.igdbbrowser.features.otp.OtpScreen
import com.skyyo.igdbbrowser.features.pagination.paging.GamesPagingScreen
import com.skyyo.igdbbrowser.features.pagination.pagingWithDatabase.GamesPagingRoomScreen
import com.skyyo.igdbbrowser.features.pagination.simple.GamesScreen
import com.skyyo.igdbbrowser.features.pagination.simpleWithDatabase.GamesRoomScreen
import com.skyyo.igdbbrowser.features.profile.*
import com.skyyo.igdbbrowser.features.samples.animations.AnimationsScreen
import com.skyyo.igdbbrowser.features.samples.bottomSheets.BottomSheetScaffoldScreen
import com.skyyo.igdbbrowser.features.samples.bottomSheets.BottomSheetScreen
import com.skyyo.igdbbrowser.features.samples.bottomSheets.ModalBottomSheetScreen
import com.skyyo.igdbbrowser.features.samples.cameraX.CameraXScreen
import com.skyyo.igdbbrowser.features.samples.forceTheme.ForceThemeScreen
import com.skyyo.igdbbrowser.features.samples.googleMap.GoogleMapScreen
import com.skyyo.igdbbrowser.features.samples.inputValidations.auto.InputValidationAutoScreen
import com.skyyo.igdbbrowser.features.samples.inputValidations.autoDebounce.InputValidationAutoDebounceScreen
import com.skyyo.igdbbrowser.features.samples.inputValidations.manual.InputValidationManualScreen
import com.skyyo.igdbbrowser.features.samples.lists.ListsScreen
import com.skyyo.igdbbrowser.features.samples.navigateWithResult.DogContactsScreen
import com.skyyo.igdbbrowser.features.samples.navigateWithResult.DogDetailsScreen
import com.skyyo.igdbbrowser.features.samples.navigateWithResult.DogFeedScreen
import com.skyyo.igdbbrowser.features.samples.photoViewer.PhotoScreen
import com.skyyo.igdbbrowser.features.samples.nestedHorizontalList.AppBarElevation
import com.skyyo.igdbbrowser.features.samples.viewPager.ViewPagerScreen
import com.skyyo.igdbbrowser.features.signIn.SignInScreen

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalPagerApi::class
)
@Composable
fun PopulatedNavHost(
    startDestination: String,
    innerPadding: PaddingValues,
    navController: NavHostController,
    onBackPressIntercepted: (() -> Unit)? = null
) = AnimatedNavHost(
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
    composable(Screens.GamesRoom.route) {
        onBackPressIntercepted?.let { BackHandler(onBack = it) }
        GamesRoomScreen()
    }
    composable(Screens.GamesPaging.route) {
        onBackPressIntercepted?.let { BackHandler(onBack = it) }
        GamesPagingScreen()
    }
    composable(Screens.GamesPagingRoom.route) {
        onBackPressIntercepted?.let { BackHandler(onBack = it) }
        GamesPagingRoomScreen()
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
    composable(Screens.CameraX.route) { CameraXScreen() }
    composable(Screens.PhotoViewer.route) { PhotoScreen() }
    bottomSheet(Screens.BottomSheet.route) { BottomSheetScreen() }
    composable(Screens.ModalBottomSheet.route) { ModalBottomSheetScreen() }
    composable(Screens.BottomSheetScaffold.route) { BottomSheetScaffoldScreen() }
    composable(Screens.ViewPager.route) { ViewPagerScreen() }
    composable(Screens.Lists.route) { ListsScreen() }
    composable(Screens.InputValidationManual.route, enterTransition = { _, _ ->
        slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400))
//                fadeIn(animationSpec = tween(2000))
    },
        exitTransition = { _, _ ->
            slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400))
        },
        popEnterTransition = { _, _ ->
            slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400))

        },
        popExitTransition = { _, _ ->
            slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400))
        }) { InputValidationManualScreen() }
    composable(Screens.InputValidationAuto.route) { InputValidationAutoScreen() }
    composable(Screens.InputValidationDebounce.route) { InputValidationAutoDebounceScreen() }
    composable(Screens.Animations.route) { AnimationsScreen() }
    composable(Screens.NestedHorizontalLists.route) { AppBarElevation() }
    composable(Screens.Otp.route) { OtpScreen() }
    composable(Screens.AutoScroll.route) { AutoScrollScreen() }
}
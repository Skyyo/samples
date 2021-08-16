package com.skyyo.igdbbrowser.application

import androidx.annotation.StringRes
import com.skyyo.igdbbrowser.R

sealed class Screens(val route: String, @StringRes val resourceId: Int = 0) {
    object AuthScreen : Screens("auth", R.string.auth)
    object GamesScreen : Screens("games", R.string.games)
    object Profile : Screens("profile", R.string.profile)

    object MapScreen : Screens("map", R.string.map)
    object ForceThemeScreen : Screens("forceTheme")
    object BottomSheetScreen : Screens("bottomSheet")
    object ModalBottomSheetScreen : Screens("modalBottomSheetContainer")
    object BottomSheetScaffoldScreen : Screens("bottomSheetScaffold")
    object ViewPagerScreen : Screens("viewPager")
    object DogFeedScreen : Screens("dogFeed", R.string.dog_feed)

}

sealed class DogDetailsGraph(val route: String) {
    //    object DogDetails : DogDetailsGraph("dogDetails/{dogId}") {
    object DogDetails : DogDetailsGraph("dogDetails") {
        fun createRoute(dogId: String) = "dogDetails/$dogId"
    }

    //    object DogContacts : DogDetailsGraph("dogContacts/{dogId}") {
    object DogContacts : DogDetailsGraph("dogContacts") {
        fun createRoute(dogId: String) = "dogContacts/$dogId"
    }

    companion object {
        const val route = "dogDetailsGraph"
    }
}

sealed class EditProfileGraph(val route: String) {

    object EditProfile : Screens("editProfile", R.string.edit_profile)
    object EditProfileConfirmation : Screens("profileConfirmation", R.string.profile_confirmation)
    object EditProfileConfirmation2 : Screens("profileConfirmation2", R.string.profile_confirmation)

    companion object {
        const val route = "profileGraph"
    }
}
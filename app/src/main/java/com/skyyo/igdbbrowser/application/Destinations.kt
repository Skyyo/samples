package com.skyyo.igdbbrowser.application

import androidx.annotation.StringRes
import com.skyyo.igdbbrowser.R

sealed class Screens(val route: String, @StringRes val resourceId: Int = 0) {
    object SignIn : Screens("signIn")
    object Games : Screens("games", R.string.games)
    object GamesRoom : Screens("gamesRoom", R.string.games_room)
    object GamesPaging : Screens("gamesRoom", R.string.games_room)
    object Profile : Screens("profile", R.string.profile)

    object Map : Screens("map", R.string.map)
    object ForceTheme : Screens("forceTheme")
    object BottomSheet : Screens("bottomSheet")
    object ModalBottomSheet : Screens("modalBottomSheetContainer")
    object BottomSheetScaffold : Screens("bottomSheetScaffold")
    object ViewPager : Screens("viewPager")
    object DogFeed : Screens("dogFeed", R.string.dog_feed)
    object Lists : Screens("lists")
    object InputValidationManual : Screens("manual")
    object InputValidationAuto : Screens("auto")
    object InputValidationDebounce : Screens("debounce")

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
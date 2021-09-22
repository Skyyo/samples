package com.skyyo.samples.application

import androidx.annotation.StringRes
import com.skyyo.samples.R

sealed class Screens(val route: String, @StringRes val resourceId: Int = 0) {
    object SampleContainer : Screens("signIn")
    object Games : Screens("games", R.string.games)
    object GamesRoom : Screens("gamesRoom", R.string.games_room)
    object GamesPaging : Screens("gamesPaging", R.string.games_paging)
    object GamesPagingRoom : Screens("gamesPagingRoom", R.string.games_paging_r)
    object Profile : Screens("profile", R.string.profile)

    object Map : Screens("map", R.string.map)
    object CameraX : Screens("cameraX")
    object ForceTheme : Screens("forceTheme")
    object BottomSheet : Screens("bottomSheet")
    object ModalBottomSheet : Screens("modalBottomSheetContainer")
    object BottomSheetScaffold : Screens("bottomSheetScaffold")
    object ViewPager : Screens("viewPager")
    object DogFeed : Screens("dogFeed", R.string.dog_feed)
    object StickyHeader : Screens("stickyHeaders")
    object InputValidationManual : Screens("manual")
    object InputValidationAuto : Screens("auto")
    object InputValidationDebounce : Screens("debounce")
    object Animations: Screens("animation")
    object Otp: Screens("otp")
    object AppBarElevation : Screens("appBarElevation")
    object AutoScroll : Screens("autoScroll")
    object Table : Screens("table")
    object ParallaxEffect : Screens("parallaxEffect")
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
package com.skyyo.samples.application

sealed class Destination(val route: String) {
    object SampleContainer : Destination("signIn")

    object Cats : Destination("cats")
    object CatsRoom : Destination("catsRoom")
    object CatsPaging : Destination("catsPaging")
    object CatsPagingRoom : Destination("catsPagingRoom")
    object BottomSheet : Destination("bottomSheet")
    object ModalBottomSheet : Destination("modalBottomSheetContainer")
    object BottomSheetScaffold : Destination("bottomSheetScaffold")
    object Map : Destination("map")
    object CameraX : Destination("cameraX")
    object ForceTheme : Destination("forceTheme")
    object ViewPager : Destination("viewPager")
    object InputValidationManual : Destination("manual")
    object InputValidationAuto : Destination("auto")
    object InputValidationDebounce : Destination("debounce")
    object AppBarElevation : Destination("appBarElevation")
    object ParallaxEffect : Destination("parallaxEffect")
    object AutoScroll : Destination("autoScroll")
    object StickyHeader : Destination("stickyHeaders")
    object Otp : Destination("otp")
    object Table : Destination("table")
    object CustomView : Destination("customViewScreen")

    object Profile : Destination("profile")
    object DogFeed : Destination("dogFeed")
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

    object EditProfile : Destination("editProfile")
    object EditProfileConfirmation : Destination("profileConfirmation")
    object EditProfileConfirmation2 : Destination("profileConfirmation2")

    companion object {
        const val route = "profileGraph"
    }
}
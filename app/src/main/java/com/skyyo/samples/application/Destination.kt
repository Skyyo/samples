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
    object InfiniteViewPager : Destination("infiniteViewPager")
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
    object MarqueeText : Destination("marqueeText")
    object Autofill : Destination("autofill")
    object DogFeed : Destination("dogFeed")
    object DogDetails : Destination("dogDetails/{dogId}") {
        fun createRoute(dogId: String) = "dogDetails/$dogId"
    }
    object DogContacts : Destination("dogContacts/{dogId}") {
        fun createRoute(dogId: String) = "dogContacts/$dogId"
    }
    object CatFeed : Destination("catFeed")
    object CatDetails : Destination("catDetails")
    object CatContacts : Destination("catContacts")
    object QrCodeScanning : Destination("qrCodeScanning")
    object QrNoPermissions : Destination("qrCodeNoPermissions")
    object ScrollAnimation1 : Destination("scrollAnimation1")
    object Snackbar : Destination("snack")
    object Snap : Destination("snap")
    object BottomBar : Destination("bottomBar")
    object Drawer : Destination("drawer")
    object Tab1 : Destination("tab1")
    object Tab2 : Destination("tab2")
    object Tab3 : Destination("tab3")
    object GradientScroll : Destination("gradientScroll")
    object NoticeableScrollableRow : Destination("noticeableScrollableRow")
    object ExoPlayerColumnReference : Destination("exoPlayerColumnRef")
    object ExoPlayerColumnIndexed : Destination("exoPlayerColumnIndexed")
    object ExoPlayerColumnAutoplay : Destination("exoPlayerColumnAutoplay")
    object ExoPlayerColumnDynamicThumb : Destination("exoPlayerColumnDynamicThumb")
    object VerticalPagerWithFling : Destination("videopager")
    object DominantColor : Destination("dominantColor")
    object Zoomable : Destination("zoomable")
    object PdfViewer : Destination("pdfViewer")
    object HealthConnect : Destination("healthConnect")
    object PrivacyPolicy : Destination("privacyPolicy")
    object DragAndDrop : Destination("dragAndDrop")
    object ImagePicker : Destination("imagePicker")
    object LanguagePicker : Destination("languagePicker")
    object CardRecognition : Destination("cardRecognition")
    object GooglePay : Destination("googlePay")
    object TextSpans : Destination("textSpans")
    object ImeAwareLazyColumn : Destination("imeAwareLazyColumn")
    object TextGradient : Destination("textGradient")
    object UserInteractionTrackingResult : Destination("userInteractionTrackingResult")
}

sealed class ProfileGraph(val route: String) {

    object Profile : Destination("profile")
    object EditProfile : Destination("editProfile")
    object ConfirmProfile : Destination("profileConfirmation")

    companion object {
        const val route = "profileGraph"
    }
}

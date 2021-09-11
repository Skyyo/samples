# Jetpack Compose samples project

1) shows a an application that interacts with [IGDB API](https://api-docs.igdb.com/#about).
2) shows a bunch of samples that are being stress tested for real world scenario. eg: google maps, bottom sheets, input validations.


# Known issues

Modal Drawer
- can't change width, [issue](https://issuetracker.google.com/issues/190879368)
- can't peek to reveal, [issue](https://issuetracker.google.com/issues/167408603)

Keyboard
- [issue](https://issuetracker.google.com/issues/187746439) with changing focus on backpress ( affecting OTP sample )

Miscellaneous
- drag & drop feature. [possible workaround](https://stackoverflow.com/questions/64913067/reorder-lazycolumn-items-with-drag-drop)
- can't have exactly 13 destinations inside NavHost. [issue](https://issuetracker.google.com/issues/195752907)
- there is no out of the box support for scroll bars as of August 19, 2021. [Sample for simple cases](https://stackoverflow.com/questions/66341823/jetpack-compose-scrollbars/68056586#68056586)
- bottomSheet destination is not preserved by default when navigating to new destination and coming back. [Workaround](https://medium.com/@theapache64/saving-bottomsheets-state-%EF%B8%8F-d9426cafbcbb)
- google maps related [issue](https://github.com/googlemaps/android-maps-utils/issues/949)
- google maps related [issue](https://issuetracker.google.com/issues/197880217)
- no way to create nested sticky headers. Workaround imo is changing design or making one lvl of the headers as a composable that animates text changes.
- Surface composable has [issue](https://issuetracker.google.com/issues/198313901) with elevation overlapping. Workaround - use [Modifier.shadow](https://developer.android.com/reference/kotlin/androidx/compose/ui/draw/package-summary#(androidx.compose.ui.Modifier).shadow(androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Shape,kotlin.Boolean)) on any composable.

# Limitations & potential issues
- We're forced to use [ProvideWindowInsets](https://google.github.io/accompanist/insets/#usage) composable as a wrapper for all composables in fragments
- There is no way to navigate from composable to fragment & share a navigation graph between them. (not an issue)
- Deep links might require lot of additional work if we need to open them in a specific bottom bar / drawer tab.
- Navigating with parcelable object might be causing issues due it's [hacky logic](https://github.com/Skyyo/IGDB-Browser/blob/e4279d7cecb50aca32aacdc712f9ed2fdd11aade/app/src/main/java/com/skyyo/igdbbrowser/extensions/NavControllerExtensions.kt#L48-L57)

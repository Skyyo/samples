# Project containing Jetpack Compose samples 
For pagination related samples it uses [IGDB API](https://api-docs.igdb.com/#about).

# Known issues

Modal Drawer
- can't change width, [issue](https://issuetracker.google.com/issues/190879368)
- can't peek to reveal, [issue](https://issuetracker.google.com/issues/167408603)

Keyboard
- [issue](https://issuetracker.google.com/issues/187746439) with changing focus on backpress ( affecting OTP sample )
- [issue](https://issuetracker.google.com/issues/199297778) with adjustPan mode not pinning to the focused textField
- [issue](https://issuetracker.google.com/issues/199561561) with keyboard being hidden when focus is shared between composables & views

Miscellaneous
- We can't drop usage of liveData completely since we can't return stateFlow from the savedStateHandle, also it is still needed for scenarios which cover flow being observed with flatMapLatest ( as a typical scenario for searching by query ). Latter [issue](https://github.com/Kotlin/kotlinx.coroutines/issues/2223)
- drag & drop feature. [possible workaround](https://stackoverflow.com/questions/64913067/reorder-lazycolumn-items-with-drag-drop)
- there is no out of the box support for scroll bars as of August 19, 2021. [Sample for simple cases](https://stackoverflow.com/questions/66341823/jetpack-compose-scrollbars/68056586#68056586)
- bottomSheet destination is not preserved by default when navigating to new destination and coming back. [Workaround](https://medium.com/@theapache64/saving-bottomsheets-state-%EF%B8%8F-d9426cafbcbb)
- google maps related [issue](https://github.com/googlemaps/android-maps-utils/issues/949)
- google maps related [issue](https://issuetracker.google.com/issues/197880217)
- no way to create nested sticky headers. Workaround imo is changing design or making one lvl of the headers as a composable that animates text changes.
- Surface composable has [issue](https://issuetracker.google.com/issues/198313901) with elevation overlapping. Workaround - use [Modifier.shadow](https://developer.android.com/reference/kotlin/androidx/compose/ui/draw/package-summary#(androidx.compose.ui.Modifier).shadow(androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Shape,kotlin.Boolean)) on any composable.
- LazyVerticalGrid seems to be really imperformant and [not adviced](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyVerticalGrid(androidx.compose.foundation.lazy.GridCells,androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.foundation.layout.Arrangement.Horizontal,kotlin.Function1)) to use. Instead use combinations of Column + Rows
- Fling breaks on skipped frames. [issue](https://issuetracker.google.com/issues/190788866)

# Limitations & potential issues
- We're forced to use [ProvideWindowInsets](https://google.github.io/accompanist/insets/#usage) composable as a wrapper for all composables in fragments
- There is no way to navigate from composable to fragment & share a navigation graph between them. (not an issue)
- Deep links might require lot of additional work if we need to open them in a specific bottom bar / drawer tab.
- Navigating with parcelable object might be causing issues due it's [hacky logic](https://github.com/Skyyo/IGDB-Browser/blob/e4279d7cecb50aca32aacdc712f9ed2fdd11aade/app/src/main/java/com/skyyo/igdbbrowser/extensions/NavControllerExtensions.kt#L48-L57)
- We need to use [setViewCompositionStrategy](https://developer.android.com/jetpack/compose/interop/interop-apis) when working with fragments

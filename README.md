# IGDB-Browser
Jetpack Compose exploration project.
1) shows a an application that interacts with [IGDB API](https://api-docs.igdb.com/#about).
2) shows a bunch of samples that are being stress tested for real world scenario. eg: google maps, bottom sheets, input validations.


# Known issues

Modal Drawer
- can't change width, [issue](https://issuetracker.google.com/issues/190879368)
- can't peek to reveal, [issue](https://issuetracker.google.com/issues/167408603)
- drag & drop feature. Not even mentioned in the road map.
- field injections in composables - seems not possible, inconvenient.
- [androidx.navigation:navigation-compose:2.4.0-alpha05](https://developer.android.com/jetpack/androidx/releases/navigation#2.4.0-alpha05) introduced non-modifiable crossfade animations, [issue](https://issuetracker.google.com/issues/172112072)

# Limitations & potential issues
- There is no way to navigate from composable to fragment & share a navigation graph between them. ( not an issues )
- Deep links might require lot of additional work if we need to open them in a specific bottom bar / drawer tab.
- Navigating with parcelable object might be causing issues due it's [hacky logic](https://github.com/Skyyo/IGDB-Browser/blob/e4279d7cecb50aca32aacdc712f9ed2fdd11aade/app/src/main/java/com/skyyo/igdbbrowser/extensions/NavControllerExtensions.kt#L48-L57)

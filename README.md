# IGDB-Browser
Jetpack Compose exploration project.
1) shows a an application that interacts with [IGDB API](https://api-docs.igdb.com/#about).
2) shows a bunch of samples that are being stress tested for real world scenario. eg: google maps, bottom sheets, input validations.


# Known issues

Modal Drawer
- can't change width, [issue](https://issuetracker.google.com/issues/190879368)
- can't peek to reveal, [issue](https://issuetracker.google.com/issues/167408603)


Miscellaneous
- ~~[androidx.navigation:navigation-compose:2.4.0-alpha05](https://developer.android.com/jetpack/androidx/releases/navigation#2.4.0-alpha05) introduced non-modifiable crossfade animations, [issue](https://issuetracker.google.com/issues/172112072)~~
- ~~navigation-compose:2.4.0-alpha07 breaks AnimatedNavHost [issue](https://github.com/google/accompanist/issues/670)~~
- drag & drop feature. Not even mentioned in the road map. [possible workaround](https://stackoverflow.com/questions/64913067/reorder-lazycolumn-items-with-drag-drop)
- can't have exactly 13 destinations inside NavHost. [issue](https://issuetracker.google.com/issues/195752907)
- there is no out of the box support for scroll bars as of August 19, 2021. [Sample for simple cases](https://stackoverflow.com/questions/66341823/jetpack-compose-scrollbars/68056586#68056586)

# Limitations & potential issues
- No field injections in composables - inconvenient.
- There is no way to navigate from composable to fragment & share a navigation graph between them. ( not an issues )
- Deep links might require lot of additional work if we need to open them in a specific bottom bar / drawer tab.
- Navigating with parcelable object might be causing issues due it's [hacky logic](https://github.com/Skyyo/IGDB-Browser/blob/e4279d7cecb50aca32aacdc712f9ed2fdd11aade/app/src/main/java/com/skyyo/igdbbrowser/extensions/NavControllerExtensions.kt#L48-L57)

# Project containing Jetpack Compose & Android samples 
For pagination & network images it uses [CATAAS](https://cataas.com/#/).

# Known issues

Paging
- Can't declare state listeners on the Flow<PagingData<Value>> or Pager or similar. We're forced to propagate important events from bottom to top instead of top to bottom. [issue](https://issuetracker.google.com/issues/200577793)

Modal Drawer
- can't change width, [issue](https://issuetracker.google.com/issues/190879368)
- can't peek to reveal, [issue](https://issuetracker.google.com/issues/167408603)

Google Maps
- [issue](https://github.com/googlemaps/android-maps-utils/issues/949)
- [issue](https://issuetracker.google.com/issues/197880217)
  
Icons
- [auto mirroring doesn't work](https://issuetracker.google.com/issues/193457331)

Keyboard
- [issue](https://issuetracker.google.com/issues/205751272), [issue](https://issuetracker.google.com/issues/192043120) with adjustPan/adjustResize mode not pinning to the focused textField. [Workaround](https://issuetracker.google.com/issues/205751272#comment3)

Miscellaneous
- We can't drop usage of liveData completely since it is still needed for scenarios which cover flow being observed with flatMapLatest ( as a typical scenario for searching by query ). Latter [issue](https://github.com/Kotlin/kotlinx.coroutines/issues/2223)
- Persistent Bottom Sheet sample doesn't work right now due to this [issue](https://issuetracker.google.com/issues/235588730)
- there is no out of the box support for scroll bars as of August 19, 2021. [Sample for simple cases](https://stackoverflow.com/questions/66341823/jetpack-compose-scrollbars/68056586#68056586)
- bottomSheet destination is not preserved by default when navigating to new destination and coming back. [Workaround](https://medium.com/@theapache64/saving-bottomsheets-state-%EF%B8%8F-d9426cafbcbb)
- no way to create nested sticky headers. Workaround imo is changing design or making one lvl of the headers as a composable that animates text changes.
- Fling breaks on skipped frames. [issue](https://issuetracker.google.com/issues/190788866)
- Need to find an alternative to onboarding guides from the view system like these libraries provided: [onboardingFlow](https://github.com/MrIceman/onboardingflow),[TapTargetView](https://github.com/KeepSafe/TapTargetView),[Spotlight](https://github.com/TakuSemba/Spotlight)
- Health connect permission resolving [issue](https://issuetracker.google.com/issues/233239418)
- Language picker has issue on Android 12 [issue](https://issuetracker.google.com/issues/236538894)
- We can handle "save to wallet" result only from activity (https://issuetracker.google.com/issues/239603300)
- Autocomplete [issue](https://issuetracker.google.com/u/1/issues/242575301)
- Custom text spans need additional API for proper RTL & BiDi support [issue](https://issuetracker.google.com/u/1/issues/237289433)
- Use ImeAwareLazyColumn until compose LazyColumn ime overlap issue not fixed (https://issuetracker.google.com/issues/192043120#comment78)
- At this point there is no ability to create instance of ContentDrawScope class, so we can't capture composable content into Bitmap, so glass blur sample will blur every composable in bounds of "blur composable", so "partial blurring" in Box will not work.

# Limitations
- There is no way to navigate from composable to fragment & share a navigation graph between them. (not an issue)
- Deep links might require lot of additional work if we need to open them in a specific bottom bar / drawer tab.
- We need to use [setViewCompositionStrategy](https://developer.android.com/jetpack/compose/interop/interop-apis) when working with fragments
- Surface composable has [issue](https://issuetracker.google.com/issues/198313901) with elevation overlapping. This is considered a proper behaviour and one of the workarounds would be using Scaffold.
  
# TODO
  
Paging ( all cases should be tested with both PagingSource & RemoteMediator versions ).
- add sample of how to use maxSize 
- scroll to top feature with maxSize (page dropping) enabled.
- paging with Grids
- allow modifying lists using selectedIds array, to ensure any modification behaviour is working well & can be properlly restored across PD ( eg. selection, checkboxes )
- immitate socket updates ( eg. stock price updates or smth )
- check out if it's a bug or misconfiguration inside paging+room sample. Upon entering the screen we're fetching twice.

Pager
- add tab indicator animation like on google weather application.
- update ViewPager sample since they've removed scroll limit. Need to see when would we really want to use it now instead of directly using LazyColumn/Row.

Google Maps
- try out the [compose maps version](https://github.com/googlemaps/android-maps-compose), and check all issues related to the maps we have right now. Especially regarding dynamic styling. 
  
Animations
- Animations typical for iOS. can be found in [olx](https://play.google.com/store/apps/details?id=ua.slando&hl=en&gl=US), [monobank](https://play.google.com/store/apps/details?id=com.ftband.mono&hl=en&gl=US). When we scroll something, toolbar changes content relatively to some text/icon being scrolled behind the toolbar
- add more animate on scroll animations
- add on scroll animation using animatable DP
- enhance parallax sample, show how to add snapping behaviour
- AppBar auto elevation for dark theme sample
- Container transformations. eg. small circle from bottom end of the screen floats to the center of the screen and changes it's size. Both back& forward animations should be flawless and tested across PD.
- Spolier animation on text, like in Telegram Android
- add snowflakes as modifier like [here](https://youtu.be/FgZvs1BsAxE), but snowflakes that actually look like snow.Relate to my gist [here](https://gist.github.com/Skyyo/adbc9f30f1f4a50bc587958ccd442dff), it's a working solution for view system inspired by Telegram Android hidden winter feature, circa 2019. Modifier should handle being added to different view types ( please use common sense in case there are blockers )
- Circular reveal animation. Ensure it can be used with Fragment based project, or show 2 different samples. Info: [1](https://pspdfkit.com/blog/2020/change-android-themes-with-circular-reveal-animation/), [2](https://dev.to/bmonjoie/jetpack-compose-reveal-effect-1fao), [3](https://proandroiddev.com/change-theme-dynamically-with-circular-reveal-animation-on-android-8cd574f5f0d8), [4](https://github.com/frogermcs/InstaMaterial/blob/Post-8/app/src/main/java/io/github/froger/instamaterial/ui/view/RevealBackgroundView.java#L71-L98).
There is a [library](https://github.com/canopas/Intro-showcase-view) which needs to be tried out, maybe that's exactly what is needed
- circular reveal upon changing theme
- cool transformation animations in google owl sample
- complex motion layout example ( currently it supports only 2 states so might be left for later )
- animate shape form, eg from circle to star


Uncategorized
- Draw / hide something using coordinates. Look into ```onGloballyPositioned``` modifier
- How to do custom shaped ripples
- Staggered grid example
- https://github.com/Skyyo/drawing-floating-objects-inside-view in compose
- draggable sample like [here](https://proandroiddev.com/jetpack-compose-calculator-ui-4dfa2ab9048e). Sample which allows to drag elements in 1 screen from top, right & bottom for example.
- add reselect bottomBar tab listener and dispatch this event to the composable.
- sample of how to handle exclusion of back gesture areas. Sample should contain a LazyRow, which has it's sides excluded from the system gesture invocation.
- DownloadManager sample. Should cover all cases (internet connection loss, fresh boot etc, cancellation) Compare with WorkManager, and describe pros & cons.
- [Baseline Profile](https://developer.android.com/studio/profile/baselineprofiles#creating-profile-rules). Measure the impact on dummy flows
- check the issue with compose & svg's. Icons don't mirror?
  
# License
```
MIT License

Copyright (c) 2021 Denis Rudenko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.```

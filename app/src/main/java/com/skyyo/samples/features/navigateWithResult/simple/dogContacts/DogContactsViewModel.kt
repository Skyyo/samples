package com.skyyo.samples.features.navigateWithResult.simple.dogContacts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.getBackStackStateHandle
import com.skyyo.samples.features.navigateWithResult.simple.dogFeed.DOG_STATUS_KEY
import com.skyyo.samples.features.navigationCores.bottomBar.WITH_BOTTOM_BAR_KEY
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogContactsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    handle: SavedStateHandle,
) : ViewModel() {

    private val withBottomBar = handle.get<Boolean>(WITH_BOTTOM_BAR_KEY)
    val dogId: String = requireNotNull(handle["dogId"])

    @Suppress("MagicNumber")
    fun popToDogFeed() {
        navigationDispatcher.emit(withBottomBar) { navController ->
            val dogFeedScreenSavedStateHandle = navController.getBackStackStateHandle(Destination.DogFeed.route)
            dogFeedScreenSavedStateHandle[DOG_STATUS_KEY] = "adopted ${System.nanoTime() / 1000f}"
            navController.popBackStack(Destination.DogFeed.route, false)
        }
    }
}

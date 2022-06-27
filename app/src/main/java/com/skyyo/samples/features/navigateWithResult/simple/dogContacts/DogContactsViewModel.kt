package com.skyyo.samples.features.navigateWithResult.simple.dogContacts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.getBackStackStateHandle
import com.skyyo.samples.features.navigateWithResult.simple.dogFeed.DOG_STATUS_KEY
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DogContactsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    handle: SavedStateHandle,
) : ViewModel() {

    val dogId: String = requireNotNull(handle["dogId"])

    fun popToDogFeed() {
        navigationDispatcher.emit { navController ->
            val dogFeedScreenSavedStateHandle = navController.getBackStackStateHandle(Destination.DogFeed.route)
            dogFeedScreenSavedStateHandle[DOG_STATUS_KEY] = "adopted ${System.nanoTime()/1000f}"
            navController.popBackStack(Destination.DogFeed.route, false)
        }
    }
}

package com.skyyo.samples.features.navigateWithResult.simple.dogContacts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.observeBackStackStateHandle
import com.skyyo.samples.features.navigateWithResult.simple.dogFeed.DOG_STATUS_KEY
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogContactsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    handle: SavedStateHandle,
) : ViewModel() {

    val dogId: String = requireNotNull(handle["dogId"])
    private lateinit var dogFeedHandle: SavedStateHandle

    init {
        navigationDispatcher.observeBackStackStateHandle(Destination.DogFeed.route) {
            dogFeedHandle = it
        }
    }

    fun popToDogFeed() = navigationDispatcher.emit {
        dogFeedHandle[DOG_STATUS_KEY] = "adopted"
        it.popBackStack(Destination.DogFeed.route, false)
    }
}

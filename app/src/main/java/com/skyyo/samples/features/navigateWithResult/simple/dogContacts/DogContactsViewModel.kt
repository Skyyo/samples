package com.skyyo.samples.features.navigateWithResult.simple.dogContacts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.setNavigationResult
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogContactsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    val dogId: String = requireNotNull(handle.get("dogId"))

    fun popToDogFeed() = navigationDispatcher.emit {
        it.setNavigationResult(
            route = Destination.DogFeed.route,
            key = "dogStatus",
            result = "adopted"
        )
        it.popBackStack(Destination.DogDetails.route, true)
    }

}

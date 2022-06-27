package com.skyyo.samples.features.navigateWithResult.simple.dogFeed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.observeNavigationResult
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val DOG_STATUS_KEY = "dogStatus"

@HiltViewModel
class DogFeedViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    val dogStatus = handle.getStateFlow(DOG_STATUS_KEY, "")

    init {
        observeDogStatusResult()
    }

    private fun observeDogStatusResult() {
        navigationDispatcher.observeNavigationResult(viewModelScope, DOG_STATUS_KEY, "") {
            handle[DOG_STATUS_KEY] = it
        }
    }

    fun goDogDetails() = navigationDispatcher.emit {
        it.navigate(Destination.DogDetails.createRoute("2211"))
    }
}

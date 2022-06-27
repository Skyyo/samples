package com.skyyo.samples.features.navigateWithResult.withObject.catFeed

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.models.Dog
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.extensions.observeNavigationResult
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val CAT_KEY = "cat"

@HiltViewModel
class CatFeedViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    //this is the result from CatContacts screen
    val cat = handle.getStateFlow<Dog?>(CAT_KEY, null)

    init {
        observeCatResult()
    }

    private fun observeCatResult() {
        navigationDispatcher.observeNavigationResult<Dog?>(viewModelScope, CAT_KEY, null) {
            handle[CAT_KEY] = it
        }
    }

    fun goCatDetails() = navigationDispatcher.emit {
        it.navigateWithObject(
            route = Destination.CatDetails.route,
            arguments = bundleOf(CAT_KEY to Dog(99, "Kit"))
        )
    }

}

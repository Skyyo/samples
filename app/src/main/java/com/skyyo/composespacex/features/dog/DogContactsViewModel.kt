package com.skyyo.composespacex.features.dog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.composespacex.application.DogDetailsGraph
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.extensions.setNavigationResult
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
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
            route = Screens.DogFeedScreen.route,
            key = "dogStatus",
            result = "adopted"
        )
        it.popBackStack(DogDetailsGraph.DogDetails.route, true)
    }

}

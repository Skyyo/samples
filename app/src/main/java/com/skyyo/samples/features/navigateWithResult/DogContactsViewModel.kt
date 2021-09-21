package com.skyyo.samples.features.navigateWithResult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.DogDetailsGraph
import com.skyyo.samples.application.Screens
import com.skyyo.samples.application.models.local.Dog
import com.skyyo.samples.extensions.setNavigationResult
import com.skyyo.samples.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogContactsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

//    val dogId: String = requireNotNull(handle.get("dogId"))
    val dog: Dog = requireNotNull(handle.get("dog"))

    fun popToDogFeed() = navigationDispatcher.emit {
        it.setNavigationResult(
            route = Screens.DogFeed.route,
            key = "dog",
//            result = "adopted"
            result = dog
        )
        it.popBackStack(DogDetailsGraph.DogDetails.route, true)
    }

}
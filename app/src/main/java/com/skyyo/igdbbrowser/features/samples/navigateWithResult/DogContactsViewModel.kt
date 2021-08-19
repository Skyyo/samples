package com.skyyo.igdbbrowser.features.samples.navigateWithResult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.igdbbrowser.application.DogDetailsGraph
import com.skyyo.igdbbrowser.application.Screens
import com.skyyo.igdbbrowser.application.models.local.Dog
import com.skyyo.igdbbrowser.extensions.setNavigationResult
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
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

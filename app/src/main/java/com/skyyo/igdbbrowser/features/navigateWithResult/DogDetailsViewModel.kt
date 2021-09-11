package com.skyyo.igdbbrowser.features.navigateWithResult

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.igdbbrowser.application.DogDetailsGraph
import com.skyyo.igdbbrowser.application.models.local.Dog
import com.skyyo.igdbbrowser.extensions.navigateWithObject
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogDetailsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    //    val dogId: String = requireNotNull(handle.get("dogId"))
    val dog: Dog = requireNotNull(handle.get("dog"))

    fun goContacts() = navigationDispatcher.emit {
        it.navigate(DogDetailsGraph.DogContacts.createRoute("3333"))
    }

    fun goContactsWithObject() = navigationDispatcher.emit {
        it.navigateWithObject(
            route = DogDetailsGraph.DogContacts.route,
            arguments = bundleOf("dog" to dog)
        )
    }

}

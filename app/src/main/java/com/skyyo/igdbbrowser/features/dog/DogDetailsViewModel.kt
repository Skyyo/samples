package com.skyyo.igdbbrowser.features.dog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.igdbbrowser.application.DogDetailsGraph
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogDetailsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    val dogId: String = requireNotNull(handle.get("dogId"))

    fun goContacts() = navigationDispatcher.emit {
        it.navigate(DogDetailsGraph.DogContacts.createRoute("3333"))
    }

}

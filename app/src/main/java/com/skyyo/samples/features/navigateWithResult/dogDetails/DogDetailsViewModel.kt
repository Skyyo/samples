package com.skyyo.samples.features.navigateWithResult.dogDetails

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.models.Dog
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.utils.NavigationDispatcher
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
        it.navigate(Destination.DogContacts.createRoute("3333"))
    }

    fun goContactsWithObject() = navigationDispatcher.emit {
        it.navigateWithObject(
            route = Destination.DogContacts.route,
            arguments = bundleOf("dog" to dog)
        )
    }

}

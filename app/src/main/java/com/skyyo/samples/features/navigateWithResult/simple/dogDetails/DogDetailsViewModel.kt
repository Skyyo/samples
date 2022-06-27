package com.skyyo.samples.features.navigateWithResult.simple.dogDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogDetailsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    handle: SavedStateHandle,
) : ViewModel() {

    val dogId: String = requireNotNull(handle["dogId"])

    fun goContacts() = navigationDispatcher.emit {
        it.navigate(Destination.DogContacts.createRoute("3333"))
    }

}

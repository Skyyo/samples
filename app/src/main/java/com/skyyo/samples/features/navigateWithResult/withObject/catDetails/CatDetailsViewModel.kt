package com.skyyo.samples.features.navigateWithResult.withObject.catDetails

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
class CatDetailsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    val cat: Dog = requireNotNull(handle["cat"])

    fun goCatContacts() = navigationDispatcher.emit {
        it.navigateWithObject(
            route = Destination.CatContacts.route,
            arguments = bundleOf("cat" to cat)
        )
    }
}

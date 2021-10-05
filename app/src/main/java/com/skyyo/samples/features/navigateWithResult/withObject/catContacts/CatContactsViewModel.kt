package com.skyyo.samples.features.navigateWithResult.withObject.catContacts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.models.Dog
import com.skyyo.samples.extensions.setNavigationResult
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatContactsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    val cat: Dog = requireNotNull(handle.get("cat"))

    fun popToCatFeed() = navigationDispatcher.emit {
        it.setNavigationResult(
            route = Destination.CatFeed.route,
            key = "cat",
            result = cat
        )
        it.popBackStack(Destination.CatDetails.route, true)
    }

}

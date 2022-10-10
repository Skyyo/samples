package com.skyyo.samples.features.navigateWithResult.withObject.catDetails

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.models.Dog
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.features.navigationCores.bottomBar.WITH_BOTTOM_BAR_KEY
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    private val withBottomBar = handle.get<Boolean>(WITH_BOTTOM_BAR_KEY)
    val cat: Dog = requireNotNull(handle["cat"])

    fun goCatContacts() = navigationDispatcher.emit(withBottomBar) {
        it.navigateWithObject(
            route = Destination.CatContacts.route,
            arguments = bundleOf("cat" to cat, WITH_BOTTOM_BAR_KEY to withBottomBar)
        )
    }
}

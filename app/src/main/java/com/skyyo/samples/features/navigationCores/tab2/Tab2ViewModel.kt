package com.skyyo.samples.features.navigationCores.tab2

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.features.navigationCores.bottomBar.WITH_BOTTOM_BAR_KEY
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class Tab2ViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {
    fun goDogFeed() = navigationDispatcher.bottomBarNavControllerEmit {
        it.navigateWithObject(
            route = Destination.DogFeed.route,
            arguments = bundleOf(WITH_BOTTOM_BAR_KEY to true)
        )
    }
}

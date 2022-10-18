package com.skyyo.samples.features.navigateWithResult.simple.dogDetails

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.features.navigationCores.bottomBar.WITH_BOTTOM_BAR_KEY
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val START_ANIMATION_RECT_KEY = "startAnimationRect"
const val END_ANIMATION_RECT_KEY = "endAnimationRect"

@HiltViewModel
class DogDetailsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    handle: SavedStateHandle,
) : ViewModel() {

    private val withBottomBar = handle.get<Boolean>(WITH_BOTTOM_BAR_KEY)
    val dogId: String = requireNotNull(handle["dogId"])

    fun goContacts() = navigationDispatcher.emit(withBottomBar) {
        it.navigateWithObject(
            route = Destination.DogContacts.createRoute("3333"),
            arguments = bundleOf(WITH_BOTTOM_BAR_KEY to withBottomBar)
        )
    }
}

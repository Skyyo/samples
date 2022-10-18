package com.skyyo.samples.features.navigateWithResult.simple.dogFeed

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toAndroidRect
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.extensions.observeNavigationResult
import com.skyyo.samples.features.navigateWithResult.simple.dogDetails.END_ANIMATION_RECT_KEY
import com.skyyo.samples.features.navigateWithResult.simple.dogDetails.START_ANIMATION_RECT_KEY
import com.skyyo.samples.features.navigationCores.bottomBar.WITH_BOTTOM_BAR_KEY
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val DOG_STATUS_KEY = "dogStatus"

@HiltViewModel
class DogFeedViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    private val withBottomBar = handle.get<Boolean>(WITH_BOTTOM_BAR_KEY)
    val dogStatus = handle.getStateFlow(DOG_STATUS_KEY, "")

    init {
        observeDogStatusResult()
    }

    private fun observeDogStatusResult() {
        navigationDispatcher.observeNavigationResult(
            coroutineScope = viewModelScope,
            key = DOG_STATUS_KEY,
            initialValue = "",
            withBottomBar = withBottomBar
        ) { handle[DOG_STATUS_KEY] = it }
    }

    fun onStartAnimationRectChanged(startAnimationRect: Rect) = navigationDispatcher.emit(withBottomBar) {
        it.currentBackStackEntry!!.savedStateHandle[START_ANIMATION_RECT_KEY] = startAnimationRect.toAndroidRect().flattenToString()
    }

    fun onEndAnimationRectChanged(endAnimationRect: Rect) = navigationDispatcher.emit(withBottomBar) {
        it.currentBackStackEntry!!.savedStateHandle[END_ANIMATION_RECT_KEY] = endAnimationRect.toAndroidRect().flattenToString()
    }

    fun goDogDetails() = navigationDispatcher.emit(withBottomBar) {
        it.navigateWithObject(
            route = Destination.DogDetails.createRoute("2211"),
            arguments = bundleOf(WITH_BOTTOM_BAR_KEY to withBottomBar)
        )
    }
}

package com.skyyo.samples.features.navigateWithResult.withObject.catContacts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.models.Dog
import com.skyyo.samples.extensions.getBackStackStateHandle
import com.skyyo.samples.features.navigateWithResult.withObject.catFeed.CAT_KEY
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatContactsViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    handle: SavedStateHandle,
) : ViewModel() {

    val cat: Dog = requireNotNull(handle["cat"])
    private lateinit var catFeedHandle: SavedStateHandle

    init {
        navigationDispatcher.getBackStackStateHandle(Destination.CatFeed.route) {
            catFeedHandle = it
        }
    }

    fun popToCatFeed() = navigationDispatcher.emit {
        catFeedHandle[CAT_KEY] = cat
        it.popBackStack(Destination.CatFeed.route, false)
    }

}

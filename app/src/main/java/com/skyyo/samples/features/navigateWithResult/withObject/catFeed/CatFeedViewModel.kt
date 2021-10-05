package com.skyyo.samples.features.navigateWithResult.withObject.catFeed

import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.models.Dog
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.extensions.observeNavigationResult
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatFeedViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    //this is the result from CatContacts screen
    val cat = MutableLiveData<Dog>()

    init {
        observeCatResult()
    }

    private fun observeCatResult() {
        navigationDispatcher.emit {
            viewModelScope.launch {
                it.observeNavigationResult<Dog>("cat")?.collect {
                    cat.value = it
                }
            }
        }
    }

    fun goCatDetails() = navigationDispatcher.emit {
        it.navigateWithObject(
            route = Destination.CatDetails.route,
            arguments = bundleOf("cat" to Dog(99, "Kit"))
        )
    }

}

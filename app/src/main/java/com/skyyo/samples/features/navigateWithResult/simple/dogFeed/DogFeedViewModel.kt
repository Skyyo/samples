package com.skyyo.samples.features.navigateWithResult.simple.dogFeed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.Destination
import com.skyyo.samples.extensions.observeNavigationResult
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogFeedViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val handle: SavedStateHandle,
) : ViewModel() {

    //this is the result from DogContacts composable
    val dogStatus = MutableLiveData<String>()

    init {
        observeDogStatusResult()
    }

    private fun observeDogStatusResult() {
        navigationDispatcher.emit {
            viewModelScope.launch {
                it.observeNavigationResult<String>("dogStatus")?.collect {
                    dogStatus.value = it
                }
            }
        }
    }

    fun goDogDetails() = navigationDispatcher.emit {
        it.navigate(Destination.DogDetails.createRoute("2211"))
    }
}

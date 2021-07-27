package com.skyyo.composespacex.features.dog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.composespacex.application.DogDetailsGraph
import com.skyyo.composespacex.extensions.getFirstFlowValueNavigationResult
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
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
                dogStatus.value = it.getFirstFlowValueNavigationResult<String>("dogStatus")
            }
        }
    }

    fun goDogAdopt(dogId: String) = navigationDispatcher.emit {
        it.navigate(DogDetailsGraph.DogDetails.createRoute(dogId))
    }

}

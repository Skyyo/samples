package com.skyyo.composespacex.features.samples

import androidx.lifecycle.ViewModel
import com.skyyo.composespacex.application.DogDetailsGraph
import com.skyyo.composespacex.application.Screens
import com.skyyo.composespacex.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SamplesViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
) : ViewModel() {

//    fun goModalDrawer() = navigationDispatcher.emit {
//        it.navigate(Screens.NavigationDrawerScreen.route)
//    }

}

package com.skyyo.igdbbrowser.features.profile

import androidx.lifecycle.ViewModel
import com.skyyo.igdbbrowser.application.EditProfileGraph
import com.skyyo.igdbbrowser.extensions.log
import com.skyyo.igdbbrowser.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedProfileViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
) : ViewModel() {


    fun goProfileConfirmation() {
        navigationDispatcher.emit { it.navigate(EditProfileGraph.EditProfileConfirmation.route) }
    }

    fun goProfileConfirmation2() {
        navigationDispatcher.emit { it.navigate(EditProfileGraph.EditProfileConfirmation2.route) }
    }
}

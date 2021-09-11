package com.skyyo.samples.features.profile

import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.EditProfileGraph
import com.skyyo.samples.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileSharedViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
) : ViewModel() {


    fun goProfileConfirmation() {
        navigationDispatcher.emit { it.navigate(EditProfileGraph.EditProfileConfirmation.route) }
    }

    fun goProfileConfirmation2() {
        navigationDispatcher.emit { it.navigate(EditProfileGraph.EditProfileConfirmation2.route) }
    }
}

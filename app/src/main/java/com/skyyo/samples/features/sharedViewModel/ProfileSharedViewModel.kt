package com.skyyo.samples.features.sharedViewModel

import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.ProfileGraph
import com.skyyo.samples.extensions.log
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileSharedViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
) : ViewModel() {

    init {
        log("ProfileSharedViewModel $this")
    }

    fun goEditProfile() {
        navigationDispatcher.emit { it.navigate(ProfileGraph.EditProfile.route) }
    }

    fun popToStart() {
        navigationDispatcher.emit {
            it.popBackStack(Destination.SampleContainer.route, false)
        }
    }

    fun goProfileConfirmation() {
        navigationDispatcher.emit { it.navigate(ProfileGraph.ConfirmProfile.route) }
    }

    override fun onCleared() {
        super.onCleared()
        log("clearing ProfileSharedViewModel")
    }
}

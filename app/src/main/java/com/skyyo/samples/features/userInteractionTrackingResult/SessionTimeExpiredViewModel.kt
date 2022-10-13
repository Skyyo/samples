package com.skyyo.samples.features.userInteractionTrackingResult

import androidx.lifecycle.ViewModel
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionTimeExpiredViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val userIdlingSessionManager: IdlingSessionManager
) : ViewModel() {

    fun onContinueButtonClick() {
        userIdlingSessionManager.startSession()
        navigationDispatcher.emit { it.popBackStack() }
    }

    fun onQuitButtonClick() {
        userIdlingSessionManager.stopSession()
    }
}

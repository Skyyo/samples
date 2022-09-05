package com.skyyo.samples.features.userInteractionTrackingResult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.activity.MILLIS_IN_SECOND
import com.skyyo.samples.application.activity.SESSION_EXTRA_TIME_SECONDS
import com.skyyo.samples.utils.NavigationDispatcher
import com.skyyo.samples.utils.eventDispatchers.UserIdlingSessionEventDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ARE_YOU_STILL_HERE_TIME = "areYouStillHereTime"

@HiltViewModel
class UserInteractionTrackingResultViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val navigationDispatcher: NavigationDispatcher,
    private val userIdlingSessionEventDispatcher: UserIdlingSessionEventDispatcher
) : ViewModel() {

    val areYouStillHereTime = handle.getStateFlow(
        ARE_YOU_STILL_HERE_TIME,
        SESSION_EXTRA_TIME_SECONDS * 1f
    )
    private var sessionTimeJob: Job? = null

    init {
        startCountdown()
    }

    private fun startCountdown() {
        sessionTimeJob?.cancel()
        sessionTimeJob = viewModelScope.launch(Dispatchers.Default) {
            delay(MILLIS_IN_SECOND)
            while (areYouStillHereTime.value > 0) {
                handle[ARE_YOU_STILL_HERE_TIME] = areYouStillHereTime.value - 1
                delay(MILLIS_IN_SECOND)
            }
            if (areYouStillHereTime.value == 0f) userIdlingSessionEventDispatcher.stopSession()
        }
    }

    fun onContinueClick() {
        userIdlingSessionEventDispatcher.startSession()
        goBack()
    }

    fun onQuitClick() {
        userIdlingSessionEventDispatcher.stopSession()
    }

    private fun goBack() {
        navigationDispatcher.emit { it.popBackStack() }
    }
}

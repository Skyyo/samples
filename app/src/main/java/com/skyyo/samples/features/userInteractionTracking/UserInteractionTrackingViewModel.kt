package com.skyyo.samples.features.userInteractionTracking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.utils.eventDispatchers.UserIdlingSessionEventDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val LOGIN = "login"
private const val PASSWORD = "password"
private const val IS_AUTHORIZED = "isAuthorized"
private const val CORRECT_LOGIN = "Admin"
private const val CORRECT_PASS = "admin"

@HiltViewModel
class UserInteractionTrackingViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val userIdlingSessionEventDispatcher: UserIdlingSessionEventDispatcher
) : ViewModel() {

    val login = handle.getStateFlow(LOGIN, "")
    val password = handle.getStateFlow(PASSWORD, "")
    val isAuthorized = handle.getStateFlow<Boolean?>(IS_AUTHORIZED, null)

    fun onLoginChange(input: String) {
        handle[LOGIN] = input
    }

    fun onPasswordChange(input: String) {
        handle[PASSWORD] = input
    }

    fun onLoginClick() {
        handle[IS_AUTHORIZED] = login.value == CORRECT_LOGIN && password.value == CORRECT_PASS
        userIdlingSessionEventDispatcher.startSession()
    }
}
